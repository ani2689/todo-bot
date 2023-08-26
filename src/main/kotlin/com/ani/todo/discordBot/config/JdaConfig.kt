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
        .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
        .addEventListeners(botListener)
        .setActivity(Activity.playing("해야 할 일 재촉"))
        .build()
        .awaitReady()

    @Bean
    fun jda() = jda

    @Bean
    fun botToken() = botToken


    init {
        jda.updateCommands()

        jda.upsertCommand("도움말", "봇의 명령어를 가져옵니다.").queue()
        jda.upsertCommand("할일", "대상의 할 일 목록을 가져옵니다.")
            .addOptions(OptionData(OptionType.USER, "대상", "대상을 지정해 주세요.")
                .setRequired(false))
            .queue()
        jda.upsertCommand("할일추가", "할 일을 추가합니다.")
            .addOptions(OptionData(OptionType.STRING, "할일", "추가할 할 일을 작성해 주세요.")
                .setRequired(true))
            .queue()
        jda.upsertCommand("할일완료", "할 일을 완료합니다").queue()
        jda.upsertCommand("데일리", "데일리를 작성합니다.")
            .addOptions(
                OptionData(OptionType.STRING, "어제한일", "어제 한 일을 작성해 주세요.").setRequired(true),
                OptionData(OptionType.STRING, "오늘할일", "오늘 할 일을 작성해 주세요.").setRequired(true),
                OptionData(OptionType.STRING, "어려웠던점", "어려웠던 점을 작성해 주세요.").setRequired(true),
                OptionData(OptionType.STRING, "공유", "링크를 남겨주세요.").setRequired(false)
            ).queue()
        jda.upsertCommand("알람추가", "알람을 추가합니다.")
            .addOptions(
                OptionData(OptionType.STRING, "제목", "알람의 제목을 설정합니다.").setRequired(true),
                OptionData(OptionType.STRING, "시간", "알람을 보낼 시간을 설정합니다.").setRequired(true),
                OptionData(OptionType.CHANNEL, "채널", "알람을 보낼 채널을 설정합니다.").setRequired(true),
                OptionData(OptionType.ROLE, "역할", "알람을 보낼 대상의 역할을 설정합니다.").setRequired(false),
                OptionData(OptionType.STRING, "내용", "알람의 내용을 설정합니다.").setRequired(false)
            ).queue()
        jda.upsertCommand("알람삭제", "알람을 삭제합니다.")
            .addOptions(
                OptionData(OptionType.CHANNEL, "채널", "지울 알람이 있는 채널을 설정합니다.").setRequired(true)
            ).queue()


    }


}