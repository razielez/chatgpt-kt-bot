package com.razielez.chatgpt.app.infra.gpt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "chatgpt")
data class ChatGptProperties(
    var endpoint: String = "",
    @NestedConfigurationProperty
    var token: Token = Token(),
    var proxyAddr: String? = null,
    var timeout: Long? = 120,
    var useGpt4Prefix: String? = null,
)

data class Token(
    var v: String = "",
    var w: Int? = 1,
)