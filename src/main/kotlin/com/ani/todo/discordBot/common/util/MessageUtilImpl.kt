package com.ani.todo.discordBot.common.util

import com.ani.todo.discordBot.domain.alarm.entity.Alarm
import com.ani.todo.discordBot.domain.todo.entity.enums.TodoStatus
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.springframework.stereotype.Component
import java.awt.Color
import java.time.Instant
import net.dv8tion.jda.api.EmbedBuilder
import com.ani.todo.discordBot.domain.todo.entity.Todo

@Component
class MessageUtilImpl : MessageUtil {

    override fun info(): EmbedBuilder = EmbedBuilder()
        .setColor(Color.cyan)
        .setTitle("이제 할 일을 하자의 명령어 목록")
        .addField("/할일", "할 일 목록을 가져옵니다.", true)
        .addField("/할일추가", "할 일을 추가합니다.", true)
        .addField("/할일완료", "할 일을 완료합니다.", true)
        .addField("/데일리", "데일리를 작성합니다.", true)
        .addField("/알람추가", "알람을 추가합니다.", true)
        .addField("/알람삭제", "알람을 삭제합니다.", true)

    override fun todoList(todos: List<Todo>, user: User): EmbedBuilder {
        val doneList = todos.filter { it.status == TodoStatus.DONE }
        val stayList = todos.filter { it.status == TodoStatus.STAY }

        val stayListString = if (stayList.isNotEmpty()) "**${stayList.joinToString("\n") { it.content }}**" else ""
        val doneListString = if (doneList.isNotEmpty()) "~~${doneList.joinToString("\n") { it.content }}~~" else ""

        val todoRate = stayList.size.toDouble()/todos.size*100

        val color =  when {
            todoRate >= 70 -> Color.RED
            todoRate >= 50 -> Color.ORANGE
            todoRate >= 30 -> Color.YELLOW
            todoRate >= 0 -> Color.GREEN
            else -> Color.WHITE
        }

        val field = if(todos.isEmpty())
            "작성된 할 일이 없어요."
        else
            "$stayListString\n$doneListString"

        val description = "해야 할 일: ${stayList.size}개"

        return EmbedBuilder()
            .setAuthor(user.name, user.avatarUrl)
            .setDescription(description)
            .addField("목록", field,true)
            .setColor(color)
            .setTimestamp(Instant.now())
    }

    override fun choiceTodo(todos: List<Todo>, user: User): StringSelectMenu {
        val selectMenu = StringSelectMenu.create("todo")
            .setPlaceholder("할 일 선택")
            .setRequiredRange(1, 1)

        todos.forEach {
            selectMenu.addOption(it.content, "complete:${user.id}:${it.id}")
        }

        return selectMenu.build()
    }

    override fun choiceAlarm(alarms: List<Alarm>, user: User): StringSelectMenu? {
        val selectMenu = StringSelectMenu.create("alarm")
            .setPlaceholder("알람 선택")
            .setRequiredRange(1, 1)

        alarms.forEach {
            selectMenu.addOption(it.title, "silence:${user.id}:${it.title}:${it.channelId}")
        }

        return selectMenu.build()
    }

    override fun daily(yesterdayTask: String, todayTask: String, hardTask: String) =
        EmbedBuilder()
            .setColor(Color.WHITE)
            .addField("어제 한 일", yesterdayTask, false)
            .addField("오늘 할 일", todayTask, false)
            .addField("어려웠던 점", hardTask, false)
            .setTimestamp(Instant.now())

    override fun simpleEmbed(title: String, content: String) =
        EmbedBuilder()
            .setTitle(title)
            .setDescription(content)

}