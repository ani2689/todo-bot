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
import java.util.*

@Component
class MessageUtilImpl(
    private val todoRepository: TodoRepository
): MessageUtil {

    override fun info(): EmbedBuilder = EmbedBuilder()
        .setTitle("이제 할 일을 하자의 명령어 목록")
        .addField("/todo", "유저의 할 일 목록을 가져옵니다.", false)
        .addField("/add", "유저의 할 일을 모두 지웁니다.", false)
        .addField("/complete", "할 일을 추가 및 완료합니다.", false)
        .addField("/daily", "데일리를 작성합니다.", false)

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
            .setPlaceholder("완료할 todo를 선택하세요.")
            .setRequiredRange(1, 1)

        todoRepository.findByUserId(user.id)
                    .forEach {
                        if (it.status == TodoStatus.STAY)
                            a.addOption(it.title, it.id.toString()+":"+type+":"+user.id)
                    }


        return a.build()
    }
}