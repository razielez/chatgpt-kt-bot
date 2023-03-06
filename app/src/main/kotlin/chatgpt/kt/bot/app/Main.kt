package chatgpt.kt.bot.app

import chatgpt.kt.bot.app.gpt.ChatGptEnvProvider
import chatgpt.kt.bot.app.gpt.DefaultGptClient
import chatgpt.kt.bot.app.gpt.Message
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.App
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.webhook.WebhookResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging


private val client = DefaultGptClient(ChatGptEnvProvider())
private val channel = Channel<Event>()
private val slack = Slack.getInstance()
private val responder = SlashCommandResponseSender(slack)
private val log = KotlinLogging.logger {}

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val app = App()
    app.command("/hello") { req, ctx ->
        GlobalScope.launch {
            send(Event(ctx.responseUrl, ":wave: Hello!req: ${req.payload.text}"))
        }
        ctx.ack()
    }
    app.command("/ask") { req, ctx ->
        GlobalScope.launch {
            send(Event(ctx.responseUrl, req.payload.text))
        }
        ctx.ack()
    }
    val server = SlackAppServer(app, 10003)
    receiver()
    server.start() // http://localhost:3000/slack/events
}

@OptIn(DelicateCoroutinesApi::class)
fun send(event: Event) {
    GlobalScope.launch {
        kotlin.run {
            channel.send(event)
        }
    }
}

data class Event(
    val responseUrl: String,
    val msg: String,
)

@OptIn(DelicateCoroutinesApi::class)
private fun receiver() {
    GlobalScope.launch {
        while (true) {
            receive()
        }
    }
}

suspend fun receive() = run {
    val event = channel.receive()
    log.info { "Receive: ${event.msg}" }
    val msg = try {
        client.completions(listOf(Message("user", event.msg))).content
    } catch (e: Exception) {
        "开始摆烂..."
    }
    val reply = SlashCommandResponse.builder().text(msg).build()
    val response: WebhookResponse = responder.send(event.responseUrl, reply)
}
