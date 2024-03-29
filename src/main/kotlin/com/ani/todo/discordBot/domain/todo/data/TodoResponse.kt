package com.ani.todo.discordBot.domain.todo.data

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

data class CreateTodoResponse(
    val content: String,
    val embed: EmbedBuilder
)

data class QueryTodosResponse(
    val content: String,
    val embed: EmbedBuilder,
    val button: List<Button>
)
data class HastenTodosResponse(
    val content: String
)

data class ChoiceTodoResponse(
    val content: String,
    val selectMenu: StringSelectMenu?
)

data class CheckTodoResponse(
    val content: String,
    val embed: EmbedBuilder
)