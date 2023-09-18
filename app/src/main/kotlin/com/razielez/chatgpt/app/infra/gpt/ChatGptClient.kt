package com.razielez.chatgpt.app.infra.gpt

import com.razielez.chatgpt.app.domain.ChatSessionMessage
import com.razielez.chatgpt.app.infra.common.InternalSerializable
import com.razielez.chatgpt.app.infra.common.toJson
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.SerialName
import kotlin.properties.Delegates

interface ChatGptClient {

    @Throws(ChatGptException::class)
    fun completions(message: List<Message>): Message

    @Throws(ChatGptException::class)
    fun completionsSSE(message: List<Message>): Sequence<CompletionResp>
}


data class Message(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
    @JsonIgnore val ts: Long = System.currentTimeMillis(),
) : InternalSerializable {

    @get:JsonIgnore
    var length by Delegates.notNull<Int>()

    init {
        length = toJson().length
    }

    fun toChatMessage():ChatSessionMessage {
        return ChatSessionMessage(
            role,
            content,
            ts
        )
    }

    companion object {
        fun of(role: Role, seq: Sequence<CompletionResp>): Message {
            var content = ""
            seq.iterator().forEach { resp ->
                resp.choices[0].delta?.content?.also {
                    content += it
                }
            }
            return Message(
                role.value,
                content
            )
        }
    }
}

fun List<Message>.tokenLen(): Int = this.sumOf { it.length }

data class Delta(
    @SerialName("role") val role: String?,
    @SerialName("content") val content: String?
)

data class CompletionReq(
    @SerialName("model") val model: String,
    @SerialName("messages") val message: List<Message>,
    @SerialName("max_tokens") val maxToken: Int,
    @SerialName("temperature") val temperature: Float,
    @SerialName("top_p") val topP: Int = 1,
    @SerialName("stream") val stream: Boolean = false,
    @SerialName("frequency_penalty") val frequencyPenalty: Int = 0,
    @SerialName("presence_penalty") val presencePenalty: Int = 0,
) : InternalSerializable


data class CompletionResp(
    @SerialName("id") val id: String?,
    @SerialName("object") val objectStr: String?,
    @SerialName("created") val createTs: Int,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<ChoiceItem>,
    @SerialName("usage") val usage: Map<String, Any?>?
) : InternalSerializable

data class ChoiceItem(
    @SerialName("message") val message: Message?,
    @SerialName("delta") val delta: Delta?,
    @SerialName("index") val index: Int,
    @SerialName("finish_reason") val finishReason: String?
) : InternalSerializable

enum class Model(val value: String) {
    GPT3_5_TURBO("gpt-3.5-turbo"),
    GPT4("gpt-4"),
    GPT4_0("gpt-4-0613"),
    ;
}

enum class Role(val value: String) {
    SYSTEM("system"),
    ASSISTANT("assistant"),
    USER("user")
}

inline infix fun <reified E : Enum<E>, V> ((E) -> V).by(value: V): E {
    return enumValues<E>().first { this(it) == value }
}
