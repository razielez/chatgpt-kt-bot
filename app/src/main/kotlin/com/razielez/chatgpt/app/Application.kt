package com.razielez.chatgpt.app

import com.razielez.chatgpt.app.infra.gpt.ChatGptProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.ServletComponentScan
import java.nio.file.Files
import java.nio.file.Path


@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties(value = [ChatGptProperties::class])
open class Application

fun main(args: Array<String>) {
//    if (!args.contains("-f")) {
//        throw RuntimeException("Please use -f to force run")
//    }
//    if (!Files.exists(Path.of(args[1]))) {
//        throw RuntimeException("Config file not found")
//    }
//    val config = args[1]
//    SpringApplicationBuilder(Application::class.java)
//        .initializers({ ctx ->
//            ctx.environment.setActiveProfiles("prod")
//            ctx.addBeanFactoryPostProcessor(PropertiesBeanFactory(config))
//        })
//        .build()
//        .run(*args)
    SpringApplication.run(Application::class.java, *args)
}