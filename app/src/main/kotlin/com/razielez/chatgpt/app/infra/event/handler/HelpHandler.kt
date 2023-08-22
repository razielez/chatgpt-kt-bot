package com.razielez.chatgpt.app.infra.event.handler

import com.razielez.chatgpt.app.infra.event.SlackChatEvent
import com.razielez.chatgpt.app.infra.event.SlackEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
open class HelpHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    @Qualifier("chatBaseImpl") private val chaBase: ChatBase,
) : Handler, SlackBase by slackBase, ChatBase by chaBase {

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val display = Kind.display()
        send(se.channel, display)
        return true
    }

    override fun kind() = Kind.HELP
}