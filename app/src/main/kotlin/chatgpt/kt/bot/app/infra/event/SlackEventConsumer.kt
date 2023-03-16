package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.gpt.DefaultChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
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
    private val client: DefaultChatGptClient,
) : EventConsumer, InitializingBean {
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

    override fun afterPropertiesSet() {
        receive()
    }

}