package com.ani.todo.discordBot.domain.alarm.repository

import com.ani.todo.discordBot.domain.alarm.entity.Alarm
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AlarmRepository : CrudRepository<Alarm, Long> {
    fun findByTitleAndChannelId(title: String, channelId: String): Alarm?
    fun existsByTitleAndChannelId(title: String, channelId: String): Boolean
    fun findByChannelId(channelId: String): List<Alarm>
    fun findAllByTime(time: String): List<Alarm>
}