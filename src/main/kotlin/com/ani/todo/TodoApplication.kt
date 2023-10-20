package com.ani.todo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import javax.annotation.PostConstruct

@EnableScheduling
@SpringBootApplication
class TodoApplication
fun main(args: Array<String>) {
    runApplication<TodoApplication>(*args)
}

@PostConstruct
fun started(){
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
}