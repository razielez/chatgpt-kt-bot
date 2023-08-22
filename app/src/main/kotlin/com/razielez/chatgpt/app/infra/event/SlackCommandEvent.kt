package com.razielez.chatgpt.app.infra.event

import com.razielez.chatgpt.app.infra.event.handler.Kind

data class SlackCommandEvent(
    val responseUrl: String,
    val msg: String,
    val kind: Kind
) : SlackEvent {
    override fun msg(): String = msg
    override fun kind() = kind
}