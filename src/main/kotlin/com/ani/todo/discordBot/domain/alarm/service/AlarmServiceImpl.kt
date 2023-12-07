package com.ani.todo.discordBot.domain.alarm.service

import com.ani.todo.discordBot.domain.alarm.data.*
import com.ani.todo.discordBot.domain.alarm.entity.Alarm
import com.ani.todo.discordBot.domain.alarm.repository.AlarmRepository
import com.ani.todo.discordBot.global.error.DiscordException
import com.ani.todo.discordBot.global.util.MessageUtil
import net.dv8tion.jda.api.entities.channel.ChannelType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AlarmServiceImpl(
    private val alarmRepository: AlarmRepository,
    private val messageUtil: MessageUtil
) : AlarmService {
    @Transactional(rollbackFor = [Exception::class])
    override fun createAlarm(createAlarmRequest: CreateAlarmRequest): CreateAlarmResponse {
        val channel = createAlarmRequest.channel
        val time = createAlarmRequest.time.split(":")
        val title = createAlarmRequest.title
        val content = createAlarmRequest.content
        val roleId = createAlarmRequest.roleId

        if (channel.type != ChannelType.TEXT)
            throw DiscordException("유효한 타입의 채널이 아니에요.")

        if (time.size != 2)
            throw DiscordException("유효한 시간 양식이 아니에요.\n00:00 양식에 맞춰 입력해주세요.")

        val hours = time[0].toTime(24)
        val minutes = time[1].toTime(60)

        if (minutes % 5 != 0)
            throw DiscordException("유효한 시간 양식이 아니에요.\n5분 단위로 입력해주세요.")

        if (alarmRepository.existsByTitleAndChannelId(title, channel.id))
            throw DiscordException("채널에 이미 같은 제목의 알람이 존재해요.")

        val timeFormat = String.format("%02d:%02d", hours, minutes)

        val alarm = Alarm(
            channelId = channel.id,
            title = title,
            content = content,
            roleId = roleId,
            time = timeFormat
        )

        alarmRepository.save(alarm)

        val response = CreateAlarmResponse(
            content = "알람 설정이 완료되었어요. 매일 **$time** 에 **$title** 알람이 울릴 거예요!"
        )

        return response
    }

    @Transactional(readOnly = true)
    override fun queryAlarms(queryAlarmsRequest: QueryAlarmsRequest): QueryAlarmsResponse {
        val channel = queryAlarmsRequest.channel
        val user = queryAlarmsRequest.user


        if (channel.type != ChannelType.TEXT)
            throw DiscordException("유효한 타입의 채널이 아니에요.")

        val alarms = alarmRepository.findByChannelId(channel.id)
            .ifEmpty { throw DiscordException("채널에 알람이 존재하지 않아요.") }

        val selectMenu = messageUtil.choiceAlarm(alarms, user)

        val response = QueryAlarmsResponse(
            content = "지울 알람을 선택해주세요.",
            selectMenu = selectMenu
        )

        return response
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun deleteAlarm(deleteAlarmRequest: DeleteAlarmRequest): DeleteAlarmResponse {
        val title = deleteAlarmRequest.title
        val channel = deleteAlarmRequest.channelId

        val alarm = alarmRepository.findByTitleAndChannelId(title, channel)
            ?: throw DiscordException("알람을 찾지 못했어요.")

        alarmRepository.delete(alarm)

        val response = DeleteAlarmResponse(
            content = "$title 알람이 삭제되었어요."
        )

        return response
    }

    private fun String.toTime(max: Int): Int{
        val time = this.toIntOrNull()
            ?: throw DiscordException("유효한 시간 입력이 아니에요.\n숫자로 작성해주세요.")

        if(time !in 0..max)
            throw DiscordException("유효한 시간 입력이 아니에요.\n범위를 벗어났어요.")

        return time
    }
}