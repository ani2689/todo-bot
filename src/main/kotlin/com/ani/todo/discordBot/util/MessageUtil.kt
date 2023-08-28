package com.ani.todo.discordBot.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

interface MessageUtil {
    fun info(): EmbedBuilder
    fun todoList(user: User): EmbedBuilder
    fun choiceTodo(user: User, type: String): StringSelectMenu?
    fun choiceAlarm(channelId: String, user: User, type: String): StringSelectMenu?
    fun choiceTime(user: User): StringSelectMenu?
    fun daily(yesterdayTask: String, todayTask: String, hardTask: String): EmbedBuilder
}
