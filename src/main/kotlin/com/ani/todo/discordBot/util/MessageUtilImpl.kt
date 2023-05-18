package com.ani.todo.discordBot.util

import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.repository.TodoRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.ActionRow
import org.springframework.stereotype.Component

@Component
class MessageUtilImpl(
    private val todoRepository: TodoRepository
): MessageUtil {
    override fun info(): EmbedBuilder = EmbedBuilder()
        .addField("!할 일", "유저의 할 일 목록을 가져옵니다.", false)
        .addField("+{추가 및 완료할 할 일 이름}", "할 일을 추가 및 완료합니다. 아래의 이모지를 눌러 조작하세요.", false)

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
            .addField("할 일 목록", when(doneList.isEmpty()&&stayList.isEmpty()) {
                true ->     "작성된 할 일이 없어요."
                false ->    stayListString + doneListString
            },true)
    }
}