package com.ani.todo.discordBot.todo.exception

enum class ErrorCode (
    val title: String,
    val status: Int
){
    INVALID_COMMAND("유효한 명령어가 아니에요.", 400),

}