package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.Role
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
open class SysRoleHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    @Qualifier("chatBaseImpl") private val chatBase: ChatBase,
) : Handler, SlackBase by slackBase, ChatBase by chatBase {
    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val user = findUser(se.from)
        val sessionId = user.profile.email
        val content = completions(sessionId, se.parsedMsg(), Role.SYSTEM)
        send(sessionId, content)
        return true
    }

    override fun kind() = Kind.SYS_ROLE
}