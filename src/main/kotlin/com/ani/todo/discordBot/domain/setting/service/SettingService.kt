package com.ani.todo.discordBot.domain.setting.service

import com.ani.todo.discordBot.domain.setting.data.HelpResponse

interface SettingService {
    fun help(): HelpResponse
}