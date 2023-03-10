package chatgpt.kt.bot.app.common

import chatgpt.kt.bot.app.gpt.ChatGptEnvProvider
import chatgpt.kt.bot.app.gpt.DefaultGptClient
import chatgpt.kt.bot.app.gpt.Message
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.response.ResponseTypes
import com.slack.api.model.Attachment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging

open class SlackEventConsumer(
    private val channel: Channel<SlackEvent>
) : EventConsumer {
    private val client = DefaultGptClient(ChatGptEnvProvider())
    private val slack = Slack.getInstance()
    private val responder = SlashCommandResponseSender(slack)

    private val log = KotlinLogging.logger { }

    @OptIn(DelicateCoroutinesApi::class)
    override fun receive() {
        GlobalScope.launch {
            while (true) {
                val event = channel.receive()
                log.info { "Receive: ${event.msg}" }
                val msg = try {
                    client.completions(listOf(Message("user", event.msg))).content
                } catch (e: Exception) {
                    log.error { "completions error $e" }
                    "开始摆烂..."
                }
                val reply = """
                        Prompt: ${event.msg}
                        $msg
                        """.trimIndent()
                responder.send(
                    event.responseUrl,
                    SlashCommandResponse.builder()
                        .text(reply)
                        .build()
                )
            }
        }
    }

}