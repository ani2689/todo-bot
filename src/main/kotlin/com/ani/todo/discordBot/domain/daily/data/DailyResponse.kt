package com.ani.todo.discordBot.domain.daily.data

import net.dv8tion.jda.api.EmbedBuilder

data class CreateDailyResponse (
    val content: String,
    val embed: EmbedBuilder
)