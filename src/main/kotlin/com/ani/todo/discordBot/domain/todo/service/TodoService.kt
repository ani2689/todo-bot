package com.ani.todo.discordBot.domain.todo.service

import com.ani.todo.discordBot.domain.todo.data.*

interface TodoService {
    fun createTodo(request: CreateTodoRequest): CreateTodoResponse
    fun queryTodos(request: QueryTodosRequest): QueryTodosResponse
    fun choiceTodo(request: ChoiceTodoRequest): ChoiceTodoResponse
    fun checkTodo(request: CheckTodoRequest): CheckTodoResponse
}