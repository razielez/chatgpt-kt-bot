package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.domain.dao.ChatSessionDao
import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.ChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.Role
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Suppress("NAME_SHADOWING")
@Component
open class ChatHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    private val chatSessionDao: ChatSessionDao,
    private val chatGptClient: ChatGptClient,
) : Handler, SlackBase by slackBase {
    private val log = KotlinLogging.logger { }
    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val user = findUser(se.from)
        val sessionId = user.profile.email
        try {
            chatSessionDao.with(
                sessionId,
                se.parsedMsg(),
                func = {
                    val seq = chatGptClient.completionsSSE(it)
                    var content = ""
                    var ts: String? = null
                    seq.iterator().forEach { resp ->
                        resp.choices[0].delta?.content?.also { t ->
                            content += t
                            ts = edit(se.channel, content, ts)
                        }
                    }
                    Message(Role.USER.value, content)
                }
            )
        } catch (e: Exception) {
            log.error { "request gpt failed! $e" }
            return false
        }
        return true
    }


    override fun kind() = Kind.CHAT
}