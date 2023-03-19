package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.domain.ChatSession
import chatgpt.kt.bot.app.infra.event.handler.Kind

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
