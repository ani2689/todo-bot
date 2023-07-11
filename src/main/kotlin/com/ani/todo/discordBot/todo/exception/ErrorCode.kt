package com.ani.todo.discordBot.todo.exception

enum class ErrorCode (
    val title: String,
    val status: Int
){
    INVALID_COMMAND("유효한 명령어가 아니에요.", 400), USER_NOT_FOUND("유효한 유저를 찾을 수 없어요.", 404)

}