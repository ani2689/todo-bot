package com.ani.todo.discordBot.domain.alarm.schedule

import com.ani.todo.discordBot.domain.alarm.repository.AlarmRepository
import com.ani.todo.discordBot.common.annotation.Scheduler
import net.dv8tion.jda.api.JDA
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Scheduler
class AlarmSchedule(
    private val alarmRepository: AlarmRepository,
    private val jda: JDA
) {
    @Scheduled(cron = "0 0/5 * * * *", zone = "Asia/Seoul")
    fun call() {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val time = LocalTime.now().format(formatter)

        println("현재 시간 : $time")

        alarmRepository.findAllByTime(time)
            .map { alarm ->
                val channel = jda.getTextChannelById(alarm.channelId)
                    ?: alarmRepository.delete(alarm)
                        .let { System.err.println("채널이 존재하지 않아요. info : [ channelId = ${alarm.channelId} ]") }
                        .let { return }

                val content = alarm.content ?: ""
                val roleMention = alarm.roleId?.let {
                    jda.getRoleById(it)?.asMention
                } ?: ""
                val title = alarm.title

                channel.sendMessage(
                    "$roleMention\n\n" +
                        "🔔 **$title**\n" +
                        "$content\n"
                ).queue()
            }
    }
}