package chatgpt.kt.bot.app

import chatgpt.kt.bot.app.gpt.ChatGptEnvProvider
import chatgpt.kt.bot.app.gpt.DefaultGptClient
import chatgpt.kt.bot.app.gpt.Message
import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.request.builtin.SlashCommandRequest

private val client = DefaultGptClient(ChatGptEnvProvider())

fun main() {
    val app = App()

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!req: ${req.payload.text}")
    }
    app.command("/ask") { req, ctx ->
        ctx.ack(
            doAsk(req, ctx)
        )
    }
    val server = SlackAppServer(app, 10003)
    server.start() // http://localhost:3000/slack/events
}

fun doAsk(req: SlashCommandRequest, ctx: SlashCommandContext): String {
    return try {
        client.completions(listOf(Message("user", req.payload.text))).content
    } catch (e: Exception) {
        "开始摆烂..."
    }
}
