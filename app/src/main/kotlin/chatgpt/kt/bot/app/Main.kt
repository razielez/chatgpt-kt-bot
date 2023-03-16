package chatgpt.kt.bot.app

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletComponentScan


@SpringBootApplication
@ServletComponentScan
open class Main

fun main(args: Array<String>) {
    SpringApplication.run(Main::class.java, *args)
}