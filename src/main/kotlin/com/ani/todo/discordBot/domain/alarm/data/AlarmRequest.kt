package com.ani.todo.discordBot.domain.alarm.data

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion

data class CreateAlarmRequest(
    val channel: GuildChannelUnion,
    val time: String,
    val title: String,
    val content: String?,
    val roleId: String?
)

data class QueryAlarmsRequest(
    val channel: GuildChannelUnion,
    val user: User
)

data class DeleteAlarmRequest(
    val title: String,
    val channelId: String
)