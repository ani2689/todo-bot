package com.ani.todo.discordBot.util

import com.ani.todo.discordBot.todo.repository.AlarmRepository
import com.ani.todo.discordBot.todo.repository.TodoRepository
import net.dv8tion.jda.api.JDA
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

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

    @Scheduled(cron = "0 30 8 * * *", zone = "Asia/Seoul")
    fun callAlarm() {
        alarmRepository.findAll()
            .map {
                when (val channel = jda.getTextChannelById(it.channelId)) {
                    null -> alarmRepository.delete(it)
                    else -> {
                        val content = when(it.content) {
                            null -> ""
                            else -> it.content
                        }
                        if (it.role == null || jda.getRoleById(it.role!!) == null)
                            channel.sendMessage(
                                "🔔 **" + it.title + "**" + "\n" +
                                        "\n" +
                                        content
                            ).queue()
                        else
                            channel.sendMessage(
                                jda.getRoleById(it.role!!)!!.asMention + "\n" +
                                        "🔔 **" + it.title + "**" + "\n" +
                                        "\n" +
                                        content
                            ).queue()
                    }

                }
            }
    }
}