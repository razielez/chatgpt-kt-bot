package chatgpt.kt.bot.app.infra.gpt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openai")
data class ChatGptProperties(
    var token: List<Token>,
    var proxyAddr: String?,
    var timeout: Long? = 120,
    var arr: List<Int>?
)

data class Token(
    var v: String,
    var w: Int? = 1,
)