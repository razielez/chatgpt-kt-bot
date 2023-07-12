package chatgpt.kt.bot.app.infra.gpt

import chatgpt.kt.bot.app.infra.common.Serializable
import chatgpt.kt.bot.app.infra.common.toJson
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.properties.Delegates

interface ChatGptClient {

    @Throws(ChatGptException::class)
    fun completions(message: List<Message>): Message

    @Throws(ChatGptException::class)
    fun completionsSSE(message: List<Message>): Sequence<CompletionResp>
}


data class Message(
    @JsonProperty("role") val role: String,
    @JsonProperty("content") val content: String,
    @JsonIgnore val ts: Long = System.currentTimeMillis(),
) : Serializable {

    @get:JsonIgnore
    var length by Delegates.notNull<Int>()

    init {
        length = toJson().length
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
    @JsonProperty("role") val role: String?,
    @JsonProperty("content") val content: String?
)

data class CompletionReq(
    @JsonProperty("model") val model: String,
    @JsonProperty("messages") val message: List<Message>,
    @JsonProperty("max_tokens") val maxToken: Int,
    @JsonProperty("temperature") val temperature: Float,
    @JsonProperty("top_p") val topP: Int = 1,
    @JsonProperty("stream") val stream: Boolean = false,
    @JsonProperty("frequency_penalty") val frequencyPenalty: Int = 0,
    @JsonProperty("presence_penalty") val presencePenalty: Int = 0,
) : Serializable


data class CompletionResp(
    @JsonProperty("id") val id: String?,
    @JsonProperty("object") val objectStr: String?,
    @JsonProperty("created") val createTs: Int,
    @JsonProperty("model") val model: String,
    @JsonProperty("choices") val choices: List<ChoiceItem>,
    @JsonProperty("usage") val usage: Map<String, Any?>?
) : Serializable

data class ChoiceItem(
    @JsonProperty("message") val message: Message?,
    @JsonProperty("delta") val delta: Delta?,
    @JsonProperty("index") val index: Int,
    @JsonProperty("finish_reason") val finishReason: String?
) : Serializable

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
