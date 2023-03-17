package chatgpt.kt.bot.app.infra.slack

import chatgpt.kt.bot.app.infra.event.EventPublisher
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.ChatGptProperties
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.middleware.builtin.RequestVerification
import com.slack.api.bolt.request.Request
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
    private val chatGptProperties: ChatGptProperties,
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
            val event = SlackEvent(ctx.responseUrl, ":wave: Hello!req: ${req.payload.text}")
            eventPublisher.send(event)
            ctx.ack()
        }
        app.command("/ask") { req, ctx ->
            val event = SlackEvent(ctx.responseUrl, req.payload.text)
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
            if (text.startsWith("test")) {
                val client = ctx.client()
                val channel = event.channel
                val ts = event.ts
                log.info { "receive message at: $ts, channel: $channel, $event , from: ${ctx.requestUserId}" }
                val reaction = client.reactionsAdd { r ->
                    r.channel(channel)
                        .timestamp(ts)
                        .name("eyes")
                }
                if (!reaction.isOk) {
                    log.error { "reaction.add failed: ${reaction.error}" }
                }

                val permalink = client.chatGetPermalink { r ->
                    r.channel(channel)
                        .messageTs(ts)
                }
                if (permalink.isOk) {
                    val message = client.chatPostMessage { r ->
                        r.channel(channel)
                            .text("This is a test demo at: $permalink")
                            .unfurlLinks(true)
                    }
                    if (!message.isOk) {
                        log.error { "chat.PostMsg failed: ${message.error}" }
                    }
                } else {
                    log.error { "chat.permalink failed: ${permalink.error}" }
                }
            }
            ctx.ack()
        }
        return app
    }


}