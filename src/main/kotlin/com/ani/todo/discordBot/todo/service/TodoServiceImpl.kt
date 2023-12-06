package com.ani.todo.discordBot.todo.service

import com.ani.todo.discordBot.todo.data.*
import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.repository.TodoRepository
import com.ani.todo.discordBot.util.MessageUtil
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TodoServiceImpl(
    private val messageUtil: MessageUtil,
    private val todoRepository: TodoRepository
) : TodoService {
    override fun createTodo(request: CreateTodoRequest): CreateTodoResponse {
        val user = request.user
        val content = request.content

        if(todoRepository.findByUserIdAndStatus(user.id, TodoStatus.STAY).size>=25){
            return CreateTodoResponse(
                content = "해야 할 일이 너무 많아요. 남아있는 일을 끝낸 뒤 다시 시도해주세요!"
            )
        }

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

    override fun queryTodo(request: QueryTodoRequest): QueryTodoResponse{
        val user = request.user

        val content = "${user.asMention}님의 할 일 목록"
        val embed = messageUtil.todoList(user)
        val button = listOf(
            Button.success("refresh:${user.id}", "새로고침"),
            Button.secondary("hasten:${user.id}", "재촉!")
        )

        val response = QueryTodoResponse(
            content = content,
            embed = embed,
            button = button
        )

        return response
    }

    override fun choiceTodo(request: ChoiceTodoRequest): ChoiceTodoResponse {
        val user = request.user

        if(todoRepository.findByUserIdAndStatus(user.id, TodoStatus.STAY).isEmpty())
            return ChoiceTodoResponse(
                content = "완료할 할 일이 존재하지 않아요.",
                selectMenu = null
            )

        val selectMenu = messageUtil.choiceTodo(user)

        val response = ChoiceTodoResponse(
            content = "완료할 할 일을 선택해주세요.",
            selectMenu = selectMenu
        )

        return response
    }

    override fun checkTodo(request: CheckTodoRequest): CheckTodoResponse {
        val todoId = request.todoId

        val todo = todoRepository.findByIdOrNull(todoId)
            ?: return CheckTodoResponse(
                content = "완료 대기중인 todo가 존재하지 않아요."
            )

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