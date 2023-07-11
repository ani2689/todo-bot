package com.ani.todo.discordBot.util

import com.ani.todo.discordBot.todo.repository.TodoRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Schedule (
    private val todoRepository: TodoRepository
){
    @Scheduled(cron = "0 0 0 1/1 * ? *")
    fun execute(){
        todoRepository.findAll()
            .map { todoRepository.delete(it) }
    }
}