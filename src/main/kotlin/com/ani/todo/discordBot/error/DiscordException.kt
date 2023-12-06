package com.ani.todo.discordBot.error


open class DiscordException(
    override val message: String
) : RuntimeException(message)