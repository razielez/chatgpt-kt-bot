package com.razielez.chatgpt.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan


@SpringBootApplication(proxyBeanMethods = false)
@ServletComponentScan
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}