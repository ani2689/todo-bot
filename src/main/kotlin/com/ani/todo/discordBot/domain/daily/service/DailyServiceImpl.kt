package com.ani.todo.discordBot.domain.daily.service

import com.ani.todo.discordBot.domain.daily.data.CreateDailyRequest
import com.ani.todo.discordBot.domain.daily.data.CreateDailyResponse
import com.ani.todo.discordBot.global.util.MessageUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DailyServiceImpl(
    private val messageUtil: MessageUtil
) : DailyService {
    @Transactional(readOnly = true)
    override fun createDaily(createDailyRequest: CreateDailyRequest): CreateDailyResponse {
        val user = createDailyRequest.user
        val userMention = user.asMention

        val url = createDailyRequest.url

        val content = "$userMention 님의 데일리\n$url"
        val embed = messageUtil.daily(
            yesterdayTask = createDailyRequest.yesterdayTask,
            todayTask = createDailyRequest.todayTask,
            hardTask = createDailyRequest.hardTask
        ).setThumbnail(user.effectiveAvatarUrl)

        val response = CreateDailyResponse(
            content = content,
            embed = embed,
        )

        return response
    }
}