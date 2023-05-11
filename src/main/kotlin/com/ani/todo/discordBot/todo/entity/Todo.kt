package com.ani.todo.discordBot.todo.entity

import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@DynamicUpdate
class Todo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODO_ID")
    var id: Long,

    @Column(name = "USER_ID")
    val userId: String,

    @Column(columnDefinition = "TEXT")
    var title: String,

    @Enumerated(value = EnumType.STRING)
    var status: TodoStatus
){
    fun completeTodo(){
        status = TodoStatus.DONE
    }

    fun overdueTodo(){
        status = TodoStatus.OVER
    }
}