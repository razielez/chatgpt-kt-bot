package com.razielez.chatgpt.app

import org.junit.jupiter.api.Test
import java.nio.file.Path

class ApplicationKtTest {

    @Test
    fun main() {
        // project dir
        val userPath =  Path.of(System.getProperty("user.dir")).toList()
        val properties = "/" + userPath.subList(0, userPath.size-1).joinToString("/") + "/conf/application.properties.sample"
        println(properties)
        main(
            arrayOf(
                "--spring.config.location=file:///$properties"
            )
        )

    }
}