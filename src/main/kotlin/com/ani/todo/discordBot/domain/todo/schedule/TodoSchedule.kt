package com.ani.todo.discordBot.domain.todo.schedule

import com.ani.todo.discordBot.domain.todo.repository.TodoRepository
import com.ani.todo.discordBot.common.annotation.Scheduler
import org.springframework.scheduling.annotation.Scheduled

@Scheduler
class TodoSchedule(
    private val todoRepository: TodoRepository
) {
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun init(){
        todoRepository.findAll()
            .map { todoRepository.delete(it) }
    }
}