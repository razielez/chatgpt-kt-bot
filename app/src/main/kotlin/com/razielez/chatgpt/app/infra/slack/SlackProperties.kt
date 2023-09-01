package com.razielez.chatgpt.app.infra.slack

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("slack")
open class SlackProperties(
    var botToken: String? = null,
    var signingSecret: String? = null,
)