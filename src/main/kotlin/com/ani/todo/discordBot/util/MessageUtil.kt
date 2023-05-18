package com.ani.todo.discordBot.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User

interface MessageUtil {
    fun info(): EmbedBuilder
    fun todoList(user: User): EmbedBuilder
}
