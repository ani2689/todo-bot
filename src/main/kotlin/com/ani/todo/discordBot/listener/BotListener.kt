package com.ani.todo.discordBot.listener

import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.exception.ErrorCode
import com.ani.todo.discordBot.todo.repository.TodoRepository
import com.ani.todo.discordBot.util.MessageUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component

@Component
class BotListener (
    private val messageUtil: MessageUtil,
    private val todoRepository: TodoRepository
) : ListenerAdapter() {

    val prefix = '!'

    val yes = "🆗"
    val no = "🆖"
    val plus = "🆙"
    val yeah = "🆒"

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(event.author.isBot)
            return
        handleMessage(event)
    }

    private fun handleMessage(event: MessageReceivedEvent) {
        val user = event.author
        val textChannel = event.channel
        val discordMessage = event.message

        val keyword = discordMessage.contentRaw.substring(1)

        if(keyword.isEmpty()||keyword.length>255){
            buildMessage(textChannel,ErrorCode.INVALID_COMMAND)
            return
        }

        when(discordMessage.contentRaw[0]){
            prefix -> {
                when(keyword){
                    "도움말" -> buildMessage(textChannel, "명령어 목록") { messageUtil.info() }.queue()
                    "할 일" -> buildMessage(textChannel, user.name+"님의 할 일 목록") { messageUtil.todoList(user) }
                        .addActionRow (listOf(Button.success("refresh:${user.id}", "새로고침"),Button.secondary("hasten:${user.id}", "재촉!")))
                        .queue()
                    "비우기" -> {
                        discordMessage.delete().queue()
                        textChannel.sendMessage("정말 모든 TODO를 비울까요?")
                            .addActionRow(
                                listOf(
                                    Button.success("yes:${user.id}", "네!"),
                                    Button.danger("no:${user.id}", "아니요!")
                                )
                            )
                            .queue()
                    }
                    else -> {
                        val todo =  todoRepository.findByUserIdAndTitle(user.id, keyword)

                        if(todo == null)
                            discordMessage.addReaction(Emoji.fromUnicode(plus)).queue()
                        if(todo != null && todo.status!= TodoStatus.DONE){
                            discordMessage.addReaction(Emoji.fromUnicode(yes)).queue()
                        }
                        discordMessage.addReaction(Emoji.fromUnicode(no)).queue()
                    }
                }
            }
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.user!!.isBot|| event.user!! != event.retrieveMessage().complete().author)
            return

        var isBot = false
        var isUser = false

        event.retrieveMessage().complete().reactions
            .forEach{
                it.retrieveUsers().complete()
                    .forEach { user ->
                        if(user.isBot)isBot = true
                        if(user.equals(event.user)) isUser = true
                    }
                if(isBot&&isUser){
                    val message = event.retrieveMessage().complete()

                    val emoji = it.emoji.asReactionCode
                    val channel = event.channel
                    val user = event.user!!

                    val todo = message.contentDisplay.substring(1)

                    event.retrieveMessage().complete().addReaction(Emoji.fromUnicode("✅")).queue()

                    when(emoji){
                        no -> {
                        }
                        plus -> {
                            todoRepository.save(Todo(0, user.id, todo, TodoStatus.STAY))
                        }
                        yes -> {
                            todoRepository.save(todoRepository.findByUserIdAndTitle(user.id, todo)!!.completeTodo())
                        }
                        else -> channel.sendMessage(ErrorCode.INVALID_COMMAND.title).queue()
                    }
                    event.retrieveMessage().complete().delete().queue()
                    return
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
            "yes" -> {
                if(user == event.user){
                    event.message.addReaction(Emoji.fromUnicode(yeah)).queue()
                    todoRepository.findByUserId(userId)
                        .map { todoRepository.delete(it) }
                    event.message.delete().queue()
                }
            }
            "no" -> {
                if(user == event.user){
                    event.message.delete().queue()
                }
            }
            else -> buildMessage(event.channel,ErrorCode.INVALID_COMMAND).queue()
        }

    }



    fun buildMessage(textChannel: MessageChannelUnion, message: String, util: () -> (EmbedBuilder)) =
        textChannel.sendMessage(message).setEmbeds(util().build())
    fun buildMessage(textChannel: MessageChannelUnion, errorCode: ErrorCode) = textChannel.sendMessage(errorCode.title)
}