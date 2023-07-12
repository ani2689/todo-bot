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
    private val messageUtil: MessageUtil,
    private val jda: JDA
){
    @Scheduled(cron = "0 0 0 * * *")
    fun initTodo(){
        todoRepository.findAll()
            .map { todoRepository.delete(it) }
    }

    @Scheduled(cron = "30 8 * * * *")
    fun callAlarm() {
        alarmRepository.findAll()
            .map {
                when (val channel = jda.getTextChannelById(it.channelId)) {
                    null -> alarmRepository.delete(it)
                    else ->
                        if (it.role == null || jda.getRoleById(it.role!!) == null)
                            channel.sendMessage("")
                                .setEmbeds(messageUtil.alarm(it.title, it.content).build())
                                .queue()
                        else
                            channel.sendMessage(jda.getRoleById(it.role!!)!!.asMention)
                                .setEmbeds(messageUtil.alarm(it.title, it.content).build())
                                .queue()

                }
            }
    }
}