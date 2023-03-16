package chatgpt.kt.bot.app.infra.gpt

import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationProperties("openai")
@Validated
open class ChatGptProperties(
    @field:NotEmpty var token: String = "",
    var proxyAddr: String? = null,
)