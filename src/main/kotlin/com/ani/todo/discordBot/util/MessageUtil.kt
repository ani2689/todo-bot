package com.ani.todo.discordBot.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.ActionRow

interface MessageUtil {
    fun info(): EmbedBuilder
    fun todoList(user: User): EmbedBuilder
    fun createTodo(title: String, user: User): EmbedBuilder
    fun deleteTodo(): EmbedBuilder
    fun completeTodo(): EmbedBuilder
    fun check(): ActionRow
}
