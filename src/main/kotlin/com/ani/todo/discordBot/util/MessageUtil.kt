package com.ani.todo.discordBot.util

import com.ani.todo.discordBot.todo.exception.ErrorCode
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User

interface MessageUtil {
    fun info(): EmbedBuilder
    fun todoList(user: User): EmbedBuilder
    fun createTodo(): EmbedBuilder
    fun deliteTodo(): EmbedBuilder
    fun clearTodo(): EmbedBuilder
    fun error(errorCode: ErrorCode): EmbedBuilder
    fun ckeck(): EmbedBuilder
}