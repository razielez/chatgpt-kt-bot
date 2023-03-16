package chatgpt.kt.bot.app.infra.slack

import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationProperties(prefix = "slack")
@Validated
open class SlackProperties(
    @field:NotEmpty var botToken: String = "",
    @field:NotEmpty var signingSecret: String = "",
)