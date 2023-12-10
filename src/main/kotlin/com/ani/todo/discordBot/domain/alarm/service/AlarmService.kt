package com.ani.todo.discordBot.domain.alarm.service

import com.ani.todo.discordBot.domain.alarm.data.*

interface AlarmService {
    fun createAlarm(createAlarmRequest: CreateAlarmRequest): CreateAlarmResponse
    fun queryAlarms(queryAlarmsRequest: QueryAlarmsRequest): QueryAlarmsResponse
    fun deleteAlarm(deleteAlarmRequest: DeleteAlarmRequest): DeleteAlarmResponse
}