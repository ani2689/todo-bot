package com.ani.todo.discordBot.domain.setting.data

import net.dv8tion.jda.api.EmbedBuilder

data class HelpResponse (
    val content: String,
    val embed: EmbedBuilder
)