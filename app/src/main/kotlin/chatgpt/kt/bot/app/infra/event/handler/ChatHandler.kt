package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.domain.dao.ChatSessionDao
import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.ChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.Role
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Suppress("NAME_SHADOWING")
@Component
open class ChatHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    private val chatSessionDao: ChatSessionDao,
    private val chatGptClient: ChatGptClient,
) : Handler, SlackBase by slackBase {

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val user = findUser(se.from)
        val sessionId = user.profile.email
        val buf = 15
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
                        if (content.length % buf == 0) {
                            ts = edit(se.channel, content, ts)
                        }
                    }
                }
                edit(se.channel, content, ts)
                Message(Role.USER.value, content)
            }
        )
        return true
    }


    override fun kind() = Kind.CHAT
}