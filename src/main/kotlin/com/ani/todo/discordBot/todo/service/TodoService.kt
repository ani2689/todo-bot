package com.ani.todo.discordBot.todo.service

import com.ani.todo.discordBot.todo.data.CheckTodoRequest
import com.ani.todo.discordBot.todo.data.CheckTodoResponse
import com.ani.todo.discordBot.todo.data.QueryTodoRequest
import com.ani.todo.discordBot.todo.data.QueryTodoResponse

interface TodoService {
    fun queryTodo(request: QueryTodoRequest): QueryTodoResponse
    fun checkTodo(request: CheckTodoRequest): CheckTodoResponse
}