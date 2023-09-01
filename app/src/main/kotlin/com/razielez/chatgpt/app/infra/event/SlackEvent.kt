package com.razielez.chatgpt.app.infra.event

import com.razielez.chatgpt.app.domain.ChatSession
import com.razielez.chatgpt.app.infra.event.handler.Kind

sealed interface SlackEvent {

    fun msg(): String

    fun kind(): Kind

    fun parsedMsg(): String {
        if (msg().length > ChatSession.MAX_LEN) {
            return msg().substring(0, ChatSession.MAX_LEN - 500)
        }
        return msg().substring(kind().prefix.length)
    }

}
