package chatgpt.kt.bot.app

import chatgpt.kt.bot.app.infra.gpt.ChatGptProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.ServletComponentScan


@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties(value = [ChatGptProperties::class])
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}