package com.ani.todo.discordBot.global.util

import com.ani.todo.discordBot.domain.alarm.repository.AlarmRepository
import com.ani.todo.discordBot.domain.todo.repository.TodoRepository
import net.dv8tion.jda.api.JDA
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class Schedule (
    private val todoRepository: TodoRepository,
    private val alarmRepository: AlarmRepository,
    private val jda: JDA
){
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun initTodo(){
        todoRepository.findAll()
            .map { todoRepository.delete(it) }
    }

    @Scheduled(cron = "0 0/5 * * * *", zone = "Asia/Seoul")
    fun callAlarm() {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val time = LocalTime.now().format(formatter)

        println("현재시간 : $time")

        alarmRepository.findByTime(time)
            ?.map {
                when (val channel = jda.getTextChannelById(it.channelId)) {
                    null -> alarmRepository.delete(it)
                    else -> {
                        val content = when(it.content) {
                            null -> ""
                            else -> it.content
                        }
                        if (it.roleId == null || jda.getRoleById(it.roleId) == null)
                            channel.sendMessage(
                                "🔔 **" + it.title + "**" + "\n" +
                                        "\n" +
                                        content
                            ).queue()
                        else
                            channel.sendMessage(
                                jda.getRoleById(it.roleId)!!.asMention + "\n" +
                                        "🔔 **" + it.title + "**" + "\n" +
                                        "\n" +
                                        content
                            ).queue()
                    }

                }
            }
    }
}