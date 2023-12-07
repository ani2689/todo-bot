package com.ani.todo.discordBot.domain.daily.service

import com.ani.todo.discordBot.domain.daily.data.CreateDailyRequest
import com.ani.todo.discordBot.domain.daily.data.CreateDailyResponse

interface DailyService {
    fun createDaily(createDailyRequest: CreateDailyRequest): CreateDailyResponse
}