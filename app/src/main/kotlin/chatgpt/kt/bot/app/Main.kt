package chatgpt.kt.bot.app

import chatgpt.kt.bot.app.common.SlackEvent
import chatgpt.kt.bot.app.common.SlackEventConsumer
import chatgpt.kt.bot.app.common.SlackEventPublisher
import com.slack.api.bolt.App
import com.slack.api.bolt.jetty.SlackAppServer
import kotlinx.coroutines.channels.Channel


private val channel = Channel<SlackEvent>()
private val eventPublisher = SlackEventPublisher(channel)
private val eventConsumer = SlackEventConsumer(channel)

fun main() {
    val app = App()
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
    val server = SlackAppServer(app, 10003)
    eventConsumer.receive()
    server.start()
}