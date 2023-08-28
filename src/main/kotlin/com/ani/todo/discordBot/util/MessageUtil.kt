package com.ani.todo.discordBot.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

interface MessageUtil {
    fun info(): EmbedBuilder
    fun todoList(user: User): EmbedBuilder
    fun choiceTodo(user: User): StringSelectMenu?
    fun choiceAlarm(channelId: String, user: User): StringSelectMenu?
    fun choiceTime(channelId: String, user: User, title: String, content: String?, role: String?): StringSelectMenu
    fun daily(yesterdayTask: String, todayTask: String, hardTask: String): EmbedBuilder
}
