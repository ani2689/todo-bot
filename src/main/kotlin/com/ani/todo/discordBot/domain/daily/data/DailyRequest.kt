package com.ani.todo.discordBot.domain.daily.data

import net.dv8tion.jda.api.entities.User

data class CreateDailyRequest(
    val user: User,
    val yesterdayTask: String,
    val todayTask: String,
    val hardTask: String,
    val url: String
)