package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
open class ChatHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    @Qualifier("chatBaseImpl") private val chatBase: ChatBase,
) : Handler, SlackBase by slackBase, ChatBase by chatBase {

    private val log = KotlinLogging.logger {}

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val user = findUser(se.from)
        val sessionId = user.profile.email
        val content = completions(sessionId, se.parsedMsg())
        send(se.channel, content)
        return true
    }


    override fun kind() = Kind.CHAT
}