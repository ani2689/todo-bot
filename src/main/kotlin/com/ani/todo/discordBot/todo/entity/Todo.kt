package com.ani.todo.discordBot.todo.entity

import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@DynamicUpdate
@Table(name = "todo")
data class Todo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    val id: Long = 0,

    @Column(name = "user_id")
    val userId: String,

    val content: String,

    @Enumerated(value = EnumType.STRING)
    val status: TodoStatus = TodoStatus.STAY
)