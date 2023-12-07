package com.ani.todo.discordBot.domain.alarm.entity

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@DynamicUpdate
@Table(name = "alarm")
data class Alarm (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    val id: Long = 0,

    @Column(name = "channel_id")
    val channelId: String,

    val title: String,

    val content: String?,

    val roleId: String?,

    @ColumnDefault("'08:30'")
    val time: String
)