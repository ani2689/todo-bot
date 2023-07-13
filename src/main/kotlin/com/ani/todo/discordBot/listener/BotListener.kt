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
            "도움말" -> {
                event.reply("명령어 목록")
                    .setEmbeds(messageUtil.info().build())
                    .queue()
            }
            "할일" ->  {
                val user = when(
                    val user = event.getOption("대상")
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
            "할일추가" -> {
                if(todoRepository.findByUserIdAndStatus(event.user.id, TodoStatus.STAY).size>=25){
                    event.reply("해야 할 일이 너무 많아요. 남아있는 일을 끝낸 뒤 다시 시도해주세요!")
                }else {
                    val todo = Todo(0, event.user.id, event.getOption("할일")!!.asString, TodoStatus.STAY)

                    todoRepository.save(todo)

                    event.reply("✍ :: ${todo.title}")
                        .queue()
                }
            }
            "할일완료" -> {
                if(todoRepository.findByUserIdAndStatus(event.user.id, TodoStatus.STAY).isEmpty())
                    event.reply("완료할 할 일이 존재하지 않아요.").queue()
                else {
                    val action = messageUtil.choiceTodo(event.user, "complete")
                    event.reply("완료할 할 일을 선택해 주세요.")
                        .addActionRow(action)
                        .queue()
                }
            }
            "데일리" -> {
                val yesterdayTask = event.getOption("어제한일")!!.asString
                val todayTask = event.getOption("오늘할일")!!.asString
                val hardTask = event.getOption("어려웠던점")!!.asString
                val url = when(event.getOption("공유")){
                    null -> ""
                    else -> event.getOption("공유")!!.asString
                }

                event.reply(event.user.asMention+"님의 데일리" + "\n" + url)
                    .setEmbeds(messageUtil.daily(yesterdayTask, todayTask, hardTask)
                        .setThumbnail(event.user.effectiveAvatarUrl)
                        .build())
                    .queue()

            }
            "알람추가" -> {
                val title = event.getOption("제목")!!.asString
                val channel = event.getOption("채널")!!.asChannel
                val content = when(event.getOption("내용")){
                    null -> null
                    else -> event.getOption("내용")!!.asString
                }
                val role = when(event.getOption("역할")){
                    null -> null
                    else -> event.getOption("역할")!!.asRole.id
                }

                if(event.getOption("채널")!!.channelType != ChannelType.TEXT){
                    event.reply("유효한 타입의 채널이 아니에요.").queue()
                } else if (alarmRepository.findByTitleAndChannelId(title, channel.id) != null) {
                    event.reply("채널에 이미 같은 제목의 알람이 존재해요.").queue()
                } else {
                    alarmRepository.save(Alarm(0, channel.id, title, content, role))
                    event.reply("알람 설정이 완료되었어요.").queue()
                }

            }
            "알람삭제" -> {
                val channel = event.getOption("채널")!!.asChannel.id

                if(event.getOption("채널")!!.channelType != ChannelType.TEXT){
                    event.reply("유효한 타입의 채널이 아니에요.").queue()
                }else if(alarmRepository.findByChannelId(channel).isEmpty()){
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
        val user = event.jda.retrieveUserById(userId).complete()

        if(user == null){
            event.channel.sendMessage("존재하지 않는 유저입니다.").queue()
            return
        }


        when(keyword){
            "refresh" -> event.editMessageEmbeds(buildMessage(event.channel,"새로고침") {messageUtil.todoList(user)}.embeds).queue()
            "hasten" -> {
                if(todoRepository.findByUserIdAndStatus(userId, TodoStatus.STAY).isEmpty()){
                    event.channel.sendMessage("🤷‍♀️ :: ${event.user.asMention}님이 부릅니다. ** 🎵 ${user.asMention}, 할 일 없어요? 🎵 **").queue()
                }else{
                    event.channel.sendMessage("🙋‍♀️ :: ${event.user.asMention}님이 부릅니다.  ** 🎵 ${user.asMention}? 다 울었으면 이제 할 일을 해요. 🎵 **").queue()
                }
                event.deferEdit().queue()
            }
            else -> buildMessage(event.channel,ErrorCode.INVALID_COMMAND.title).queue()
        }

    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {

        val value = event.selectedOptions.firstOrNull()!!.value.split(":")

        if(event.user.id != value[1] || event.selectedOptions.firstOrNull() == null)
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