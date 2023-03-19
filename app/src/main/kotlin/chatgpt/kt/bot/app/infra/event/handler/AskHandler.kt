package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackCommandEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.ChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component


@Component
open class AskHandler(
    private val chatGptClient: ChatGptClient,
) : Handler {
    private val slack = Slack.getInstance()
    private val responder = SlashCommandResponseSender(slack)
    private val log = KotlinLogging.logger {}


    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackCommandEvent
        val answer = try {
            chatGptClient.completions(listOf(Message("user", se.parsedMsg()))).content
        } catch (e: Exception) {
            log.error { "completions error $e" }
            "开始摆烂..."
        }
        val reply = """
                        Prompt: ${se.parsedMsg()}
                        $answer
                        """.trimIndent()
        responder.send(
            se.responseUrl,
            SlashCommandResponse.builder()
                .text(reply)
                .build()
        )
        return true
    }

    override fun kind() = Kind.ASK
}