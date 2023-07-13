package chatgpt.kt.bot.app.infra.gpt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "chatgpt")
data class ChatGptProperties(
    var endpoint: String,
    var token: Token,
    var proxyAddr: String?,
    var timeout: Long? = 120,
    var useGpt4Prefix: String? = null,
)

data class Token(
    var v: String,
    var w: Int? = 1,
)