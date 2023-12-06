package com.ani.todo.discordBot.todo.data

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

data class CreateTodoResponse(
    val content: String,
)

data class QueryTodoResponse(
    val content: String,
    val embed: EmbedBuilder,
    val button: List<Button>
)

data class ChoiceTodoResponse(
    val content: String,
    val selectMenu: StringSelectMenu?
)

data class CheckTodoResponse(
    val content: String
)