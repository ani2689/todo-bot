package com.ani.todo.discordBot.domain.todo.service

import com.ani.todo.discordBot.domain.todo.data.*
import com.ani.todo.discordBot.domain.todo.entity.Todo
import com.ani.todo.discordBot.domain.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.domain.todo.repository.TodoRepository
import com.ani.todo.discordBot.global.error.DiscordException
import com.ani.todo.discordBot.global.util.MessageUtil
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoServiceImpl(
    private val messageUtil: MessageUtil,
    private val todoRepository: TodoRepository
) : TodoService {
    @Transactional(rollbackFor = [Exception::class])
    override fun createTodo(request: CreateTodoRequest): CreateTodoResponse {
        val user = request.user
        val content = request.content

        if(todoRepository.countByUserIdAndStatus(user.id, TodoStatus.STAY)>=25)
            throw DiscordException("해야 할 일이 너무 많아요. 남아있는 일을 끝낸 뒤 다시 시도해주세요!")

        val todo = Todo(
            userId = user.id,
            content = content,
        )

        todoRepository.save(todo)

        val response = CreateTodoResponse(
            content = content
        )

        return response
    }

    @Transactional(readOnly = true)
    override fun queryTodos(request: QueryTodosRequest): QueryTodosResponse {
        val user = request.user

        val content = "${user.asMention}님의 할 일 목록"
        val embed = messageUtil.todoList(user)
        val button = listOf(
            Button.success("refresh:${user.id}", "새로고침"),
            Button.secondary("hasten:${user.id}", "재촉!")
        )

        val response = QueryTodosResponse(
            content = content,
            embed = embed,
            button = button
        )

        return response
    }

    @Transactional(readOnly = true)
    override fun hastenTodos(request: HastenTodosRequest): HastenTodosResponse {
        val receiverId = request.receiver.id
        val senderMention = request.sender.asMention
        val receiverMention = request.receiver.asMention

        val content = if(todoRepository.existsByUserIdAndStatus(receiverId, TodoStatus.STAY)){
            "🙋‍♀️ :: $senderMention 님이 부릅니다.  ** 🎵 $receiverMention ? 다 울었으면 이제 할 일을 해요. 🎵 **"
        }else{
            "🤷‍♀️ :: $senderMention 님이 부릅니다. ** 🎵 $receiverMention , 할 일 없어요? 🎵 **"
        }

        val response = HastenTodosResponse(
            content = content
        )

        return response
    }

    @Transactional(readOnly = true)
    override fun choiceTodo(request: ChoiceTodoRequest): ChoiceTodoResponse {
        val user = request.user

        if(!todoRepository.existsByUserIdAndStatus(user.id, TodoStatus.STAY))
            throw DiscordException("완료할 할 일이 존재하지 않아요.")

        val selectMenu = messageUtil.choiceTodo(user)

        val response = ChoiceTodoResponse(
            content = "완료할 할 일을 선택해주세요.",
            selectMenu = selectMenu
        )

        return response
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun checkTodo(request: CheckTodoRequest): CheckTodoResponse {
        val todoId = request.todoId

        val todo = todoRepository.findByIdOrNull(todoId)
            ?: throw DiscordException("완료할 할 일이 존재하지 않아요.")

        val checkTodo = todo.run {
            Todo(
                id = id,
                userId = userId,
                content = content,
                status = TodoStatus.DONE
            )
        }

        todoRepository.save(checkTodo)

        val response = CheckTodoResponse(
            content = "📝 :: ${checkTodo.content} 완료!"
        )

        return response
    }
}