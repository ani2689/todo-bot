package com.ani.todo.discordBot.global.util

import com.ani.todo.discordBot.domain.alarm.entity.Alarm
import com.ani.todo.discordBot.domain.todo.entity.Todo
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

interface MessageUtil {
    fun info(): EmbedBuilder
    fun todoList(todos: List<Todo>, user: User): EmbedBuilder
    fun choiceTodo(todos: List<Todo>, user: User): StringSelectMenu?
    fun choiceAlarm(alarms: List<Alarm>, user: User): StringSelectMenu?
    fun daily(yesterdayTask: String, todayTask: String, hardTask: String): EmbedBuilder
    fun simpleEmbed(title: String, content: String): EmbedBuilder
}
