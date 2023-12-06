package com.ani.todo.discordBot.global.aop.discord

import com.ani.todo.discordBot.global.error.DiscordException
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import kotlin.runCatching as runCatching

@Aspect
@Component
class DiscordAspect {

    @Pointcut("@annotation(com.ani.todo.discordBot.global.aop.discord.DiscordErrorCatch)")
    fun annotatedMethod() {}

    @Around("annotatedMethod()")
    fun discordExceptionHandler(joinPoint: ProceedingJoinPoint): Any? {
        runCatching {
            joinPoint.proceed()
        }.onFailure {
            when(it){
                is DiscordException -> {
                    val event = getEvent(joinPoint)
                    event.reply(it.message).queue()
                }
                else -> throw it
            }
        }
        return null
    }

    private fun getEvent(joinPoint: ProceedingJoinPoint): IReplyCallback =
        joinPoint.args.first { a -> a is IReplyCallback } as IReplyCallback
}