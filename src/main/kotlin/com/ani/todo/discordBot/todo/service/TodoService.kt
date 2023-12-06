package com.ani.todo.discordBot.todo.service

import com.ani.todo.discordBot.todo.data.*

interface TodoService {
    fun createTodo(request: CreateTodoRequest): CreateTodoResponse
    fun queryTodo(request: QueryTodoRequest): QueryTodoResponse
    fun choiceTodo(request: ChoiceTodoRequest): ChoiceTodoResponse
    fun checkTodo(request: CheckTodoRequest): CheckTodoResponse
}