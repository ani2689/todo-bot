package com.ani.todo.discordBot.listener

import com.ani.todo.discordBot.todo.entity.Todo
import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import com.ani.todo.discordBot.todo.exception.ErrorCode
import com.ani.todo.discordBot.todo.repository.TodoRepository
import com.ani.todo.discordBot.util.MessageUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component

@Component
class BotListener (
    private val messageUtil: MessageUtil,
    private val todoRepository: TodoRepository
) : ListenerAdapter() {

    val prefix = '!'

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(event.author.isBot)
            return
        handleMessage(event)
    }

    private fun handleMessage(event: MessageReceivedEvent) {
        val user = event.author
        val textChannel = event.channel
        val discordMessage = event.message

        val todoTitle = discordMessage.contentRaw.substring(1)

        if(todoTitle.isEmpty()||todoTitle.length>255){
            buildMessage(textChannel,ErrorCode.INVALID_COMMAND)
            return
        }

        when(discordMessage.contentRaw[0]){
            prefix -> {
                when(todoTitle){
                    "도움말" -> buildMessage("명령어 목록") { messageUtil.info() }
                    "할 일" -> buildMessage(user.name+"님의 할 일 목록") { messageUtil.todoList(user) }
                    else -> buildMessage(ErrorCode.INVALID_COMMAND)
                }.queue()
            }
            '+' -> {
                val todo =  todoRepository.findByUserIdAndTitle(user.id,todoTitle)

                if(todo == null)
                    discordMessage.addReaction(Emoji.fromUnicode("➕")).queue()
                if(todo != null && todo.status!= TodoStatus.DONE){
                    discordMessage.addReaction(Emoji.fromUnicode("✔")).queue()
                }
                discordMessage.addReaction(Emoji.fromUnicode("❌")).queue()
            }
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.user!!.isBot|| event.user!! != event.retrieveMessage().complete().author)
            return

        var isBot = false
        var isUser = false

        event.retrieveMessage().complete().reactions
            .forEach{
                it.retrieveUsers().complete()
                    .forEach { user ->
                        if(user.isBot)isBot = true
                        if(user.equals(event.user)) isUser = true
                    }
                if(isBot&&isUser){
                    val message = event.retrieveMessage().complete()

                    val emoji = it.emoji.asReactionCode
                    val channel = event.channel
                    val user = event.user

                    val todo = message.contentDisplay.substring(1)

                    event.retrieveMessage().complete().addReaction(Emoji.fromUnicode("✅")).queue()

                    when(emoji){
                        "❌" -> {
                            channel.sendMessage("명령어가 취소됐어요!").complete().delete().queue()
                        }
                        "➕" -> {
                            todoRepository.save(Todo(0, user!!.id, todo, TodoStatus.STAY))
                        }
                        "✔" -> {
                            todoRepository.save(todoRepository.findByUserIdAndTitle(user!!.id, todo)!!.completeTodo())
                        }
                        else -> channel.sendMessage(ErrorCode.INVALID_COMMAND.title).queue()
                    }
                    event.retrieveMessage().complete().delete().queue()
                    return
                }
            }
    }
}