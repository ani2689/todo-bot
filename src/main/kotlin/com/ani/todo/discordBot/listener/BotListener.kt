package com.ani.todo.discordBot.listener

import com.ani.todo.discordBot.todo.entity.Todo
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.exception.ErrorCode
import com.ani.todo.discordBot.todo.repository.TodoRepository
import com.ani.todo.discordBot.util.MessageUtil

@Component
class BotListener (
    private val messageUtil: MessageUtil,
    private val todoRepository: TodoRepository
) : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        when(event.name){
            "help" -> {
                event.reply("명령어 목록")
                    .setEmbeds(messageUtil.info().build())
                    .queue()
            }
            "todo" ->  {
                val user = when(
                    val user = event.getOption("user")
                ){
                    null -> event.user
                    else -> user.asUser
                }
                event.reply(user.name+"님의 할 일 목록")
                    .setEmbeds( messageUtil.todoList(user).build())
                    .addActionRow(
                        listOf(
                            Button.success("refresh:${user.id}", "새로고침"),
                            Button.secondary("hasten:${user.id}", "재촉!")
                        )
                    )
                    .queue()
            }
            "add" -> {
                val todo = Todo(0, event.user.id, event.getOption("todo")!!.asString, TodoStatus.STAY)

                todoRepository.save(todo)

                event.reply("☑ :: ${todo.title}")
                    .queue()
            }
            "complete" -> {
                if(todoRepository.findByUserIdAndStatus(event.user.id, TodoStatus.STAY).isEmpty())
                    event.reply("완료할 할 일이 존재하지 않아요.").queue()
                else {
                    val action = messageUtil.choiceTodo(event.user, "complete")
                    event.reply("완료할 할 일을 선택해 주세요.")
                        .addActionRow(action)
                        .queue()
                }
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val keyword  = event.button.id!!.split(":")[0]
        val userId = event.button.id!!.split(":")[1]
        val user = event.jda.getUserById(userId)


        when(keyword){
            "refresh" -> event.editMessageEmbeds(buildMessage(event.channel,"새로고침") {messageUtil.todoList(user!!)}.embeds).queue()
            "hasten" -> {
                if(todoRepository.findByUserIdAndStatus(userId, TodoStatus.STAY).isEmpty()){
                    event.channel.sendMessage("${user?.asMention}, 할 거 없어요? 🤷‍♀️").queue()
                }else{
                    event.channel.sendMessage("${user?.asMention}? 다 울었으면 이제 할 일을 해요 🙋‍♀️").queue()
                }
                event.deferEdit().queue()
            }
            else -> buildMessage(event.channel,ErrorCode.INVALID_COMMAND.title).queue()
        }

    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {

        if(event.selectedOptions.firstOrNull() == null)
            return

        val value = event.selectedOptions.firstOrNull()!!.value.split(":")

        if(event.user.id != value[2])
            return

        when(value[1]){
            "complete" -> {

                val todo = todoRepository.findById(value[0].toLong()).get().completeTodo()
                    .let { todoRepository.save(it) }

                event.message.editMessageComponents().queue()
                event.message.editMessage(MessageEditData.fromContent("✅ :: ${todo.title}")).queue()

            }
            else -> buildMessage(event.channel, ErrorCode.INVALID_COMMAND.title).queue()
        }
    }


    fun buildMessage(textChannel: MessageChannelUnion, message: String, util: () -> (EmbedBuilder)) =
        textChannel.sendMessage(message).setEmbeds(util().build())
    fun buildMessage(textChannel: MessageChannelUnion, content: String) = textChannel.sendMessage(content)
}