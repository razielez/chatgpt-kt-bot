package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackCommandEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.Message
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component


@Component
open class AskHandler(
    @Qualifier("chatBaseImpl") private val chatBase: ChatBase,
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
) : Handler, ChatBase by chatBase, SlackBase by slackBase {

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackCommandEvent
        val q = se.parsedMsg()
        val a = completions(listOf(Message("user", q)))
        val reply = "Q: $q\nA: $a"
        sendByCmd(se.responseUrl, reply)
        return true
    }

    override fun kind() = Kind.ASK
}