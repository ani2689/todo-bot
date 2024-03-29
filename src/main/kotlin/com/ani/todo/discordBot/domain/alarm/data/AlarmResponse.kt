package com.ani.todo.discordBot.domain.alarm.data

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

data class CreateAlarmResponse(
    val content: String,
    val embed: EmbedBuilder
)

data class QueryAlarmsResponse(
    val content: String,
    val selectMenu: StringSelectMenu?
)

data class DeleteAlarmResponse(
    val content: String,
    val embed: EmbedBuilder
)