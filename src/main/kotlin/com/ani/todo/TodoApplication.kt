package com.ani.todo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class TodoApplication
fun main(args: Array<String>) {
    runApplication<TodoApplication>(*args)
}
