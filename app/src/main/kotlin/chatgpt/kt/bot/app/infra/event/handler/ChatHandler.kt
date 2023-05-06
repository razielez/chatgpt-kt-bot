package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
open class ChatHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    @Qualifier("chatBaseImpl") private val chatBase: ChatBase,
) : Handler, SlackBase by slackBase, ChatBase by chatBase {

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val user = findUser(se.from)
        val sessionId = user.profile.email
        completionsSSE(sessionId, se.parsedMsg(), sender = { text ->
            edit(se.channel, text)
        })
        return true
    }


    override fun kind() = Kind.CHAT
}