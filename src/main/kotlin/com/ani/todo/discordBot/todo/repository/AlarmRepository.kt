package com.ani.todo.discordBot.todo.repository

import com.ani.todo.discordBot.todo.entity.Alarm
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AlarmRepository : CrudRepository<Alarm, Long> {
    fun findByTitleAndChannelId(title: String, channelId: String): Alarm?
    fun findByChannelId(channelId: String): List<Alarm>?

    fun findByTime(time: String): List<Alarm>?
}