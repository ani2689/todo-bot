package com.ani.todo.discordBot.todo.entity

import com.ani.todo.discordBot.todo.entity.status.TodoStatus
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "todo")
data class Todo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    var id: Long = 0,

    @Column(name = "user_id")
    var userId: String,

    var title: String,

    @Enumerated(value = EnumType.STRING)
    var status: TodoStatus
){
    fun completeTodo(): Todo{
        status = TodoStatus.DONE
        return this
    }
}