package com.ani.todo.discordBot.global.error


open class DiscordException(
    override val message: String
) : RuntimeException(message)