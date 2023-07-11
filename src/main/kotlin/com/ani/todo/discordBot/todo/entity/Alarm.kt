package com.ani.todo.discordBot.todo.entity

import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@DynamicUpdate
@Table(name = "alarm")
data class Alarm (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    var id: Long = 0,

    @Column(name = "channel_id")
    var channelId: String,

    val title: String,

    val content: String?,

    var role: String?,

)