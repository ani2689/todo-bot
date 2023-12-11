package com.ani.todo.discordBot.domain.todo.repository

import com.ani.todo.discordBot.domain.todo.entity.Todo
import com.ani.todo.discordBot.domain.todo.entity.enums.TodoStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : CrudRepository<Todo, Long> {
    fun findByUserId(userId: String): List<Todo>
    fun findByUserIdAndStatus(userId: String, status: TodoStatus): List<Todo>
    fun countByUserIdAndStatus(userId: String, status: TodoStatus): Int
    fun existsByUserIdAndStatus(userId: String, status: TodoStatus): Boolean
}