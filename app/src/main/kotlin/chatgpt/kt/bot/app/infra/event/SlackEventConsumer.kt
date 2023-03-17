package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.gpt.DefaultChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.App
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
open class SlackEventConsumer(
    private val channel: Channel<SlackEvent>,
    private val gptClient: DefaultChatGptClient,
    private val app: App,
) : EventConsumer, InitializingBean {
    private val slack = Slack.getInstance()
    private val responder = SlashCommandResponseSender(slack)

    private val log = KotlinLogging.logger { }

    @OptIn(DelicateCoroutinesApi::class)
    override fun receive() {
        GlobalScope.launch {
            while (true) {
                val event = channel.receive()
                if (log.isDebugEnabled) {
                    log.debug { "Receive: $event" }
                }
                when (event) {
                    is SlackCommandEvent -> receiveCommandEvent(event)
                    is SlackChatEvent -> receiveChatEvent(event)
                    else -> {}
                }
            }
        }
    }

    private fun receiveCommandEvent(event: SlackCommandEvent) {
        val msg = try {
            gptClient.completions(listOf(Message("user", event.msg))).content
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

    private fun receiveChatEvent(event: SlackChatEvent) {
        val client = app.client
        val permalink = client.chatGetPermalink { r ->
            r.channel(event.channel).messageTs(event.ts)
        }
        val msg = gptClient.completions(listOf(Message("user", event.msg))).content
        if (permalink.isOk) {
            val message = client.chatPostMessage { r ->
                r.channel(event.channel)
                    .text(msg)
                    .unfurlLinks(true)
            }
            if (!message.isOk) {
                log.error { "chat.PostMsg failed: ${message.error}" }
            }
        } else {
            log.error { "permalink failed! ${permalink.error}" }
        }
    }

    override fun afterPropertiesSet() {
        receive()
    }

}