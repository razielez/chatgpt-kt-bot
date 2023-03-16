package chatgpt.kt.bot.app.infra.slack

import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.event.EventPublisher
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SlackAppConfig(
    private val eventPublisher: EventPublisher<SlackEvent>,
    private val properties: SlackProperties,
) {

    @Bean
    open fun slackAppCfg(): AppConfig {
        return AppConfig.builder()
            .singleTeamBotToken(properties.botToken)
            .signingSecret(properties.signingSecret)
            .build()
    }

    @Bean
    open fun slackApp(config: AppConfig): App {
        val app = App(config)
        config.clientId?.also { app.asOAuthApp(true) }
        app.command("/hello") { req, ctx ->
            val event = SlackEvent(ctx.responseUrl, ":wave: Hello!req: ${req.payload.text}")
            eventPublisher.send(event)
            ctx.ack()
        }
        app.command("/ask") { req, ctx ->
            val event = SlackEvent(ctx.responseUrl, req.payload.text)
            eventPublisher.send(event)
            ctx.ack()
        }
        return app
    }


}