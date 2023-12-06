package com.ani.todo.discordBot.domain.todo.data

import net.dv8tion.jda.api.entities.User

data class CreateTodoRequest(
    val user: User,
    val content: String
)

data class QueryTodosRequest(
    val user: User
)

data class HastenTodosRequest(
    val sender: User,
    val receiver: User
)

data class ChoiceTodoRequest(
    val user: User
)

data class CheckTodoRequest(
    val todoId: Long
)
