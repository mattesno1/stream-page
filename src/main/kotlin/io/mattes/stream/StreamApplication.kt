package io.mattes.stream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class StreamApplication

fun main(args: Array<String>) {
	runApplication<StreamApplication>(*args)
}
