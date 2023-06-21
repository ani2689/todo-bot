package com.ani.todo.discordBot.util

import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.repository.TodoRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Component
import java.awt.Color
import java.sql.Timestamp
import java.time.Instant
import java.util.TimeZone

@Component
class MessageUtilImpl(
    private val todoRepository: TodoRepository
): MessageUtil {

    val prefix = '!'

    val yes = "✔"
    val no = "❌"
    val plus = "➕"
    
    override fun info(): EmbedBuilder = EmbedBuilder()
        .addField("${prefix}할 일", "유저의 할 일 목록을 가져옵니다.", false)
        .addField("$prefix{추가 및 완료할 TODO}", "할 일을 추가 및 완료합니다. $plus, $yes 또는 $no 를눌러 조작하세요.", false)

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

        return EmbedBuilder()
            .setAuthor(user.name,null,user.avatarUrl)
            .setDescription("해야 할 일: ${stayList.size}개")
            .addField("목록", when(doneList.isEmpty()&&stayList.isEmpty()) {
                true ->     "작성된 할 일이 없어요."
                false ->    stayListString + "\n" + doneListString
            },true)
            .setColor(Color.white)
            .setTimestamp(Instant.now())
    }
}