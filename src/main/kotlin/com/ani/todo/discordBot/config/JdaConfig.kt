package com.ani.todo.discordBot.config

import com.ani.todo.discordBot.listener.BotListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JdaConfig (
    @Value("\${discord.bot.token}")
    private val botToken: String,
){

    private val jda = JDABuilder.createDefault(botToken)
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .addEventListeners(BotListener())
        .build()
        .awaitReady()

    @Bean
    fun jda() = jda

    @Bean
    fun botToken() = botToken


}