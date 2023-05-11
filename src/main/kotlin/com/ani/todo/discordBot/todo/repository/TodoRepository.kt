package com.ani.todobot.discordBot.repository

import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import org.springframework.data.repository.CrudRepository

interface TodoRepository : CrudRepository<Todo, Long> {
    fun findByUserId(id: String): List<Todo>
    fun findByUserIdAndStatus(id: String, status: TodoStatus): List<Todo>
}