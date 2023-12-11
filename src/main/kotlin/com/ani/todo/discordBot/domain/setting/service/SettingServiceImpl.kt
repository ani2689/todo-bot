package com.ani.todo.discordBot.domain.setting.service

import com.ani.todo.discordBot.domain.setting.data.HelpResponse
import com.ani.todo.discordBot.common.util.MessageUtil
import org.springframework.stereotype.Service

@Service
class SettingServiceImpl(
    private val messageUtil: MessageUtil
) : SettingService {
    override fun help(): HelpResponse {
        val embed = messageUtil.info()
        val content = "명령어 목록"

        val response = HelpResponse(
            content = content,
            embed = embed
        )

        return response
    }
}