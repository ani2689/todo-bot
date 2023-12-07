package com.ani.todo.discordBot.global.listener

import com.ani.todo.discordBot.domain.alarm.data.CreateAlarmRequest
import com.ani.todo.discordBot.domain.alarm.data.DeleteAlarmRequest
import com.ani.todo.discordBot.domain.alarm.data.QueryAlarmsRequest
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.messages.MessageEditData
import org.springframework.stereotype.Component
import com.ani.todo.discordBot.domain.alarm.service.AlarmService
import com.ani.todo.discordBot.domain.daily.data.CreateDailyRequest
import com.ani.todo.discordBot.domain.daily.service.DailyService
import com.ani.todo.discordBot.domain.setting.service.SettingService
import com.ani.todo.discordBot.domain.todo.data.*
import com.ani.todo.discordBot.domain.todo.service.TodoService
import com.ani.todo.discordBot.global.aop.discord.DiscordErrorCatch
import com.ani.todo.discordBot.global.error.DiscordException

@Component
class BotListener (
    private val todoService: TodoService,
    private val alarmService: AlarmService,
    private val dailyService: DailyService,
    private val settingService: SettingService
) : ListenerAdapter() {

    @DiscordErrorCatch
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "도움말" -> {
                val response = settingService.help()
                event.reply(response.content)
                    .setEmbeds(response.embed.build())
                    .queue()
            }

            "할일" -> {
                val request = event.run {
                    QueryTodosRequest(
                        user = getOption("대상")?.asUser ?: user
                    )
                }
                val response = todoService.queryTodos(request)

                event.reply(response.content)
                    .setEmbeds(response.embed.build())
                    .addActionRow(response.button)
                    .queue()
            }

            "할일추가" -> {
                val request = event.run {
                    CreateTodoRequest(
                        user = user,
                        content = getOption("할일")!!.asString
                    )
                }
                val response = todoService.createTodo(request)

                event.reply(response.content)
                    .queue()
            }

            "할일완료" -> {
                val request = event.run {
                    ChoiceTodoRequest(
                        user = user
                    )
                }
                val response = todoService.choiceTodo(request)

                event.reply(response.content)
                    .addActionRow(response.selectMenu)
                    .queue()
            }

            "데일리" -> {
                val request = event.run {
                    CreateDailyRequest(
                        user = event.user,
                        yesterdayTask = getOption("어제한일")!!.asString,
                        todayTask = event.getOption("오늘할일")!!.asString,
                        hardTask = event.getOption("어려웠던점")!!.asString,
                        url = event.getOption("공유")?.asString ?: ""
                    )
                }
                val response = dailyService.createDaily(request)

                event.reply(response.content)
                    .setEmbeds(response.embed.build())
                    .queue()

            }

            "알람추가" -> {
                val request = event.run {
                    CreateAlarmRequest(
                        channel = getOption("채널")!!.asChannel,
                        time = getOption("시간")!!.asString,
                        title = getOption("제목")!!.asString,
                        content = getOption("내용")?.asString,
                        roleId = getOption("역할")?.asRole?.id
                    )
                }
                val response = alarmService.createAlarm(request)
                event.reply(response.content).queue()
            }

            "알람삭제" -> {
                val request = event.run {
                    QueryAlarmsRequest(
                        channel = getOption("채널")!!.asChannel,
                        user = user
                    )
                }
                val response = alarmService.queryAlarms(request)

                event.reply(response.content)
                    .addActionRow(response.selectMenu)
                    .queue()
            }
        }
    }

    @DiscordErrorCatch
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val buttonId = event.button.id ?: return

        val value = buttonId.split(":")

        if(value.size != 2)
            return

        val keyword  = value[0]
        val sender = event.user
        val receiverId = value[1]
        val receiver = event.jda.retrieveUserById(receiverId).complete()
            ?: throw DiscordException("존재하지 않는 유저입니다.")


        when(keyword){
            "update" -> {
                val request = QueryTodosRequest(
                    user = receiver
                )
                val response = todoService.queryTodos(request)

                event.editMessageEmbeds(response.embed.build()).queue()
            }
            "hasten" -> {
                val request = HastenTodosRequest(
                    sender = sender,
                    receiver = receiver
                )
                val response = todoService.hastenTodos(request)

                event.channel.sendMessage(response.content).queue()
                event.deferEdit().queue()
            }
        }

    }

    @DiscordErrorCatch
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {

        val selectedOptions = event.selectedOptions.firstOrNull() ?: return

        val value = selectedOptions.value.split(":")

        if(event.user.id != value[1])
            return

        when(value[0]){
            "complete" -> {
                val request = CheckTodoRequest(
                    todoId = value[2].toLong(),
                )
                val response = todoService.checkTodo(request)

                event.message.editMessageComponents().queue()

                MessageEditData.fromContent(response.content)
                    .let { event.message.editMessage(it).queue() }
            }
            "silence" -> {
                val request = DeleteAlarmRequest(
                    title = value[2],
                    channelId = value[3]
                )
                val response = alarmService.deleteAlarm(request)
                event.message.editMessageComponents().queue()

                MessageEditData.fromContent(response.content)
                    .let { event.message.editMessage(it).queue() }
            }
        }
    }
}