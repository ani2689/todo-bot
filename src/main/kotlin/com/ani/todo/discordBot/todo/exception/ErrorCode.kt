package com.ani.todo.discordBot.todo.exception

enum class ErrorCode (
    val title: String,
    val status: Int
){
    INVALID_COMMAND("유효한 명령어가 아니에요.", 400),

    NOT_AUTHOR("본인만 편집할 수 있어요.",403),

    NOT_FOUND_TODO("존재하지 않는 할 일이에요.",404),
    NOT_FOUND_USER("존재하지 않는 사람이에요.",404),

    TIME_OUT("오래된 요청입니다. 봇을 다시 불러주세요.",408),

}