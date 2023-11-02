pluginManagement {
    val kotlinVersion:String by settings
    val springBootVersion:String by settings


    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version "1.1.0"
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "chatgpt-kt-bot"
include("app")
