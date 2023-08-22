package com.razielez.chatgpt.app.infra.event

import com.razielez.chatgpt.app.infra.event.handler.Kind

data class SlackChatEvent(
    val from: String,
    val msg: String,
    val kind: Kind,
    val channel: String,
    val ts: String,
    var sessionId: String? = null
) : SlackEvent {
    override fun msg(): String = msg
    override fun kind() = kind
}