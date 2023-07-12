package com.ani.todo.discordBot.listener

import com.ani.todo.discordBot.todo.entity.Alarm
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
import com.ani.todo.discordBot.todo.repository.AlarmRepository
import com.ani.todo.discordBot.todo.repository.TodoRepository
import com.ani.todo.discordBot.util.MessageUtil
import net.dv8tion.jda.api.entities.channel.ChannelType

@Component
class BotListener (
    private val messageUtil: MessageUtil,
    private val todoRepository: TodoRepository,
    private val alarmRepository: AlarmRepository
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
                event.reply("${user.asMention}님의 할 일 목록")
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

                event.reply("✍ :: ${todo.title}")
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
            "daily" -> {
                val yesterdayTask = event.getOption("yesterday_task")!!.asString
                val todayTask = event.getOption("today_task")!!.asString
                val hardTask = event.getOption("hard_task")!!.asString
                val url = when(event.getOption("share")){
                    null -> null
                    else -> event.getOption("share")!!.asString
                }

                event.reply(event.user.asMention+"님의 데일리")
                    .setEmbeds(messageUtil.dailyBox(yesterdayTask, todayTask, hardTask, url)
                        .setFooter(event.user.name, event.user.effectiveAvatarUrl)
                        .build())
                    .queue()

            }
            "loud" -> {
                val title = event.getOption("title")!!.asString
                val channel = event.getOption("channel")!!.asChannel
                val content = when(event.getOption("content")){
                    null -> null
                    else -> event.getOption("content")!!.asString
                }
                val role = when(event.getOption("role")){
                    null -> null
                    else -> event.getOption("role")!!.asRole.id
                }

                if(event.getOption("channel")!!.channelType == ChannelType.GROUP){
                    event.reply("유효한 타입의 채널이 아니에요.")
                    return
                }

                if(alarmRepository.findByTitleAndChannelId(title, channel.id) != null) {
                    event.reply("채널에 이미 같은 제목의 알람이 존재해요.").queue()
                }else {
                    alarmRepository.save(Alarm(0, channel.id, title, content, role))
                    event.reply("알람 설정이 완료되었어요.").queue()
                }

            }
            "silence" -> {

                val channel = event.getOption("channel")!!.asChannel.id


                if(alarmRepository.findByChannelId(channel) == null){
                    event.reply("채널에 알람이 존재하지 않아요.").queue()
                }else{
                    val action = messageUtil.choiceAlarm(channel, event.user, "silence")
                    event.reply("지울 알림의 제목을 선택해 주세요.")
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

        if(event.user.id != value[1])
            return

        when(value[0]){
            "complete" -> {

                val todo = todoRepository.findById(value[2].toLong()).get().completeTodo()
                    .let { todoRepository.save(it) }

                event.message.editMessageComponents().queue()
                event.message.editMessage(MessageEditData.fromContent("✅ :: ${todo.title}")).queue()

            }
            "silence" -> {
                val alarm = alarmRepository.findByTitleAndChannelId(value[2], value[3])!!

                alarmRepository.delete(alarm)

                event.message.editMessageComponents().queue()
                event.message.editMessage(MessageEditData.fromContent("❎ :: ${alarm.title}")).queue()
            }
            else -> buildMessage(event.channel, ErrorCode.INVALID_COMMAND.title).queue()
        }
    }


    fun buildMessage(textChannel: MessageChannelUnion, message: String, util: () -> (EmbedBuilder)) =
        textChannel.sendMessage(message).setEmbeds(util().build())
    fun buildMessage(textChannel: MessageChannelUnion, content: String) = textChannel.sendMessage(content)
}