package com.razielez.chatgpt.app.infra.event.handler

import com.razielez.chatgpt.app.infra.event.SlackCommandEvent
import com.razielez.chatgpt.app.infra.event.SlackEvent
import com.razielez.chatgpt.app.infra.gpt.Message
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