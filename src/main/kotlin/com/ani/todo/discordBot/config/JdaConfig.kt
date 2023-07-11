package com.ani.todo.discordBot.config

import com.ani.todo.discordBot.listener.BotListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JdaConfig (
    @Value("\${discord.bot.token}")
    private val botToken: String,
    botListener: BotListener
){

    private val jda = JDABuilder.createDefault(botToken)
        .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
        .addEventListeners(botListener)
        .setActivity(Activity.playing("해야 할 일 재촉"))
        .build()
        .awaitReady()

    @Bean
    fun jda() = jda

    @Bean
    fun botToken() = botToken


    init {
        jda.upsertCommand("help", "봇의 명령어를 가져옵니다.").queue()
        jda.upsertCommand("todo", "대상의 할 일 목록을 가져옵니다.")
            .addOptions(OptionData(OptionType.USER, "user", "대상을 지정해 주세요.")
                .setRequired(false))
            .queue()
        jda.upsertCommand("add", "할 일을 추가합니다.")
            .addOptions(OptionData(OptionType.STRING, "todo", "추가할 할 일을 작성해 주세요.")
                .setRequired(true))
            .queue()
        jda.upsertCommand("complete", "완료할 할 일을 선택합니다.").queue()
        jda.upsertCommand("daily", "데일리를 작성합니다.")
            .addOptions(
                OptionData(OptionType.STRING, "yesterday_task", "어제 한 일을 작성해 주세요.").setRequired(true),
                OptionData(OptionType.STRING, "today_task", "오늘 할 일을 작성해 주세요.").setRequired(true),
                OptionData(OptionType.STRING, "hard_task", "어려웠던 점을 작성해 주세요.").setRequired(true),
                OptionData(OptionType.STRING, "share", "링크를 남겨주세요.").setRequired(false)
            ).queue()
        jda.upsertCommand("loud", "알람을 추가합니다.")
            .addOptions(
                OptionData(OptionType.STRING, "title", "알람의 제목을 설정합니다.").setRequired(true),
                OptionData(OptionType.CHANNEL, "channel", "알람을 보낼 채널을 설정합니다.").setRequired(true),
                OptionData(OptionType.ROLE, "role", "알람을 보낼 대상의 역할을 설정합니다.").setRequired(false),
                OptionData(OptionType.STRING, "content", "알람의 내용을 설정합니다.").setRequired(false)
            ).queue()
        jda.upsertCommand("silence", "알람을 삭제합니다.")
            .addOptions(
                OptionData(OptionType.STRING, "title", "지울 알람의 제목을 설정합니다.").setRequired(true),
                OptionData(OptionType.STRING, "channel", "지울 알람이 있는 채널을 설정합니다.").setRequired(true)
            ).queue()


    }


}