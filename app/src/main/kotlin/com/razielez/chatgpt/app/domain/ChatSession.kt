package com.razielez.chatgpt.app.domain

import com.razielez.chatgpt.app.infra.common.InternalSerializable
import com.razielez.chatgpt.app.infra.common.toJson
import com.razielez.chatgpt.app.infra.gpt.Message
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.properties.Delegates

@Serializable
data class ChatSessionMessage(
    val role: String,
    val content: String,
    val ts: Long = System.currentTimeMillis(),
) : InternalSerializable {
    @get:JsonIgnore
    var length by Delegates.notNull<Int>()

    init {
        length = toJson().length
    }

    fun toMessage():Message {
        return Message(
            role,
            content,
            ts
        )
    }
}

fun List<ChatSessionMessage>.tokenLen(): Int = this.sumOf { it.length }

@Serializable
class ChatSession(
    val sessionId: String,
    val messages: LinkedList<ChatSessionMessage>,
) : InternalSerializable {

    fun append(message: Message): ChatSession {
        messages.addLast(message.toChatMessage())
        while (messages.tokenLen() > MAX_LEN) {
            messages.removeFirst()
        }
        return this
    }

    fun removeExpire(now: Long): ChatSession {
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
        const val EXPIRE_MS = 1000  * 5 * 60  // 5 min
        const val MAX_LEN = 2900 // 支持最大上下文长度 ~= 4000 token

        fun of(sessionId: String): ChatSession {
            return ChatSession(sessionId, LinkedList())
        }

    }
}