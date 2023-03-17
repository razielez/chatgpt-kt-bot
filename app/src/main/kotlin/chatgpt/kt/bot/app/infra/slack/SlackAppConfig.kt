package chatgpt.kt.bot.app.infra.slack

import chatgpt.kt.bot.app.infra.event.EventPublisher
import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackCommandEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.ChatGptProperties
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.request.RequestType
import com.slack.api.model.event.MessageEvent
import com.slack.api.model.event.ReactionAddedEvent
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SlackAppConfig(
    private val eventPublisher: EventPublisher<SlackEvent>,
    private val properties: SlackProperties,
) {

    private val log = KotlinLogging.logger { }

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
        app.asOAuthApp(false)
        app.use { req, resp, chain ->
            if (log.isDebugEnabled) {
                log.debug { "req: $req, resp: $resp" }
            }
            if (req.requestType.equals(RequestType.UrlVerification)) {
                resp.body = "{\"challenge\":\"${req.requestBodyAsString}\"}\n"
            }
            chain.next(req)
        }
        app.command("/hello") { req, ctx ->
            log.info { "receive: $req, ctx: $ctx" }
            val event = SlackCommandEvent(ctx.responseUrl, ":wave: Hello!req: ${req.payload.text}")
            eventPublisher.send(event)
            ctx.ack()
        }
        app.command("/ask") { req, ctx ->
            val event = SlackCommandEvent(ctx.responseUrl, req.payload.text)
            eventPublisher.send(event)
            ctx.ack()
        }

        app.event(ReactionAddedEvent::class.java) { payload, ctx ->
            val event = payload.event
            if (event.reaction.equals("white_check_mark")) {
                val message = ctx.client().chatPostMessage { r ->
                    r.channel(event.item.channel)
                        .threadTs(event.item.ts)
                        .text("<@${event.user}>Thank you! We greatly appreciate your efforts :two_hearts:")
                }
                if (!message.isOk) {
                    log.error { "chat.postMsg failed: ${message.error}" }
                }
            }
            ctx.ack()
        }

        app.event(MessageEvent::class.java) { payload, ctx ->
            val event = payload.event
            val text = event.text
            if (log.isDebugEnabled) {
                log.debug { "receive: $payload, ctx: $ctx" }
            }
            val prefix = "<@${ctx.botUserId}> "
            if (text.startsWith(prefix)) {
                val from = event.user
                val msg = text.substring(prefix.length)
                val channel = event.channel
                val event = SlackChatEvent(from, msg, channel, event.ts, payload.token)
                log.info { "receive message at: ${event.ts}, channel: $channel, $event , from: ${ctx.requestUserId}" }
                eventPublisher.send(event)
            }
            ctx.ack()
        }
        return app
    }


}