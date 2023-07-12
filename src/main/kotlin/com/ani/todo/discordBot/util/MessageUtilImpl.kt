package com.ani.todo.discordBot.util

import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.repository.TodoRepository
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.springframework.stereotype.Component
import java.awt.Color
import java.time.Instant
import net.dv8tion.jda.api.EmbedBuilder
import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.repository.AlarmRepository
import java.util.*

@Component
class MessageUtilImpl(
    private val todoRepository: TodoRepository,
    private val alarmRepository: AlarmRepository
): MessageUtil {

    override fun info(): EmbedBuilder = EmbedBuilder()
        .setColor(Color.cyan)
        .setTitle("이제 할 일을 하자의 명령어 목록")
        .addField("/할일", "할 일 목록을 가져옵니다.", true)
        .addField("/할일추가", "할 일을 추가합니다.", true)
        .addField("/할일완료", "할 일을 완료합니다.", true)
        .addField("/데일리", "데일리를 작성합니다.", true)
        .addField("/알람추가", "알람을 추가합니다.", true)
        .addField("/알람삭제", "알람을 삭제합니다.", true)

    override fun todoList(user: User): EmbedBuilder {
        val doneList = ArrayList<Todo>()
        val stayList = ArrayList<Todo>()
        todoRepository.findByUserId(user.id)
            .forEach{
                when(it.status) {
                TodoStatus.DONE-> doneList.add(it)
                TodoStatus.STAY -> stayList.add(it)
            }}

        val stayListString = if (stayList.isNotEmpty()) "**${stayList.joinToString("\n") { it.title }}**" else ""
        val doneListString = if (doneList.isNotEmpty()) "~~${doneList.joinToString("\n") { it.title }}~~" else ""

        val todoRate = stayList.size.toDouble()/(doneList.size+stayList.size)*100
        val color =  when{
            todoRate >= 70 -> Color.RED
            todoRate >= 50 -> Color.ORANGE
            todoRate >= 30 -> Color.YELLOW
            todoRate >= 0 -> Color.GREEN
            else -> Color.WHITE
        }

        return EmbedBuilder()
            .setAuthor(user.name,null,user.avatarUrl)
            .setDescription("해야 할 일: ${stayList.size}개")
            .addField("목록", when(doneList.isEmpty()&&stayList.isEmpty()) {
                true ->     "작성된 할 일이 없어요."
                false ->    stayListString + "\n" + doneListString
            },true)
            .setColor(color)
            .setTimestamp(Instant.now())
    }

    override fun choiceTodo(user: User, type: String) : StringSelectMenu {

        val a = StringSelectMenu.create("todo")
            .setPlaceholder("할 일 선택")
            .setRequiredRange(1, 1)

        todoRepository.findByUserId(user.id)
                    .forEach {
                        if (it.status == TodoStatus.STAY)
                            a.addOption(it.title, type+":"+user.id+":"+it.id.toString())
                    }


        return a.build()
    }

    override fun choiceAlarm(channelId: String, user: User, type: String): StringSelectMenu? {
        val a = StringSelectMenu.create("alarm")
            .setPlaceholder("알람 선택")
            .setRequiredRange(1, 1)

        alarmRepository.findByChannelId(channelId)
            .forEach {
                a.addOption(it.title, type+":"+user.id+":"+it.title+":"+it.channelId)
            }


        return a.build()
    }

    override fun dailyBox(yesterdayTask: String, todayTask: String, hardTask: String) =
            EmbedBuilder()
                .setColor(Color.WHITE)
                .addField("어제 한 일", yesterdayTask, false)
                .addField("오늘 할 일", todayTask, false)
                .addField("어려웠던 점", hardTask, false)
                .setTimestamp(Instant.now())
}