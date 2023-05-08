package chatgpt.kt.bot.app.domain

import chatgpt.kt.bot.app.infra.common.Serializable
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.tokenLen
import java.util.*

class ChatSession(
    val sessionId: String,
    val messages: LinkedList<Message>,
) : Serializable {

    fun append(message: Message): ChatSession {
        messages.addLast(message)
        while (messages.tokenLen() > MAX_LEN) {
            messages.removeFirst()
        }
        return this
    }

    fun removeExpire(now:Long) :ChatSession{
        while (messages.isNotEmpty()) {
            if (now - messages.first.ts > EXPIRE_MS) {
                messages.removeFirst()
            } else {
                break
            }
        }
        return this
    }

    companion object {
        const val EXPIRE_MS = 1000 * 60 * 60  // 1 hour
        const val MAX_LEN = 2900 // 支持最大上下文长度 ~= 4000 token

        fun of(sessionId: String): ChatSession {
            return ChatSession(sessionId, LinkedList())
        }

    }
}