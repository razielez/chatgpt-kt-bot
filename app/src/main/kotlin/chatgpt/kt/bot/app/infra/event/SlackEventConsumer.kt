package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.gpt.DefaultChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.slack.SlackProperties
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.App
import com.slack.api.methods.request.users.UsersInfoRequest
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
    private val slackProperties: SlackProperties,
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
                    log.debug { "Receive: $event, msg: [${event.msg()}]" }
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
        val uiq = UsersInfoRequest.builder()
            .token(slackProperties.botToken)
            .user(event.from)
            .build()
        val ui = client.usersInfo(uiq)
        val email = ui.user.profile.email
        log.info { "user email: $email" }
        val permalink = client.chatGetPermalink { r ->
            r.channel(event.channel).messageTs(event.ts)
        }
        val msg = if (!event.msg.startsWith("test")) {
            gptClient.completions(listOf(Message("user", event.msg))).content
        } else {
            "this is a tset from ${event.msg}"
        }
        val message = client.chatPostMessage { r ->
            r.channel(event.channel).text(msg)
        }
        if (!message.isOk) {
            log.error { "chat.PostMsg failed: ${message.error}" }
        }
    }

    override fun afterPropertiesSet() {
        receive()
    }

}