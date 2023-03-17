package chatgpt.kt.bot.app.infra.gpt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("openai")
open class ChatGptProperties(
    var token: String = "",
    var proxyAddr: String? = null,
)