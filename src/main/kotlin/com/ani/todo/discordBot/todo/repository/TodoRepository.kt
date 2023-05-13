package com.ani.todo.discordBot.todo.repository

import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : CrudRepository<Todo, Long> {
    fun findByUserId(userId: String): List<Todo>
    fun findByUserIdAndStatus(userId: String, status: TodoStatus): List<Todo>

    fun findByUserIdAndTitle(userId: String, title: String): Todo?
}