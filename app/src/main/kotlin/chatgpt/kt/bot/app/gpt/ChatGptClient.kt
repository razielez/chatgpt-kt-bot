package chatgpt.kt.bot.app.gpt

import chatgpt.kt.bot.app.utils.JsonTools
import com.fasterxml.jackson.annotation.JsonProperty

interface Gpt3Client {

    fun completions(message: List<Message>): Message

}


data class Message(
    @field:JsonProperty("role") val role: String,
    @field:JsonProperty("content") val content: String
)

data class CompletionReq(
    @field:JsonProperty("model") val model: String,
    @field:JsonProperty("messages") val message: List<Message>,
    @field:JsonProperty("max_tokens") val maxToken: Int,
    @field:JsonProperty("temperature") val temperature: Float,
    @field:JsonProperty("top_p") val topP: Int = 1,
    @field:JsonProperty("stream") val stream: Boolean = false,
    @field:JsonProperty("frequency_penalty") val frequencyPenalty: Int = 0,
    @field:JsonProperty("presence_penalty") val presencePenalty: Int = 0,
)

fun Any.toJson(): String = JsonTools.toJson(this)


data class CompletionResp(
    @field:JsonProperty("id") val id: String,
    @field:JsonProperty("object") val objectStr: String,
    @field:JsonProperty("created") val createTs: Int,
    @field:JsonProperty("model") val model: String,
    @field:JsonProperty("choices") val choices: List<ChoiceItem>,
    @field:JsonProperty("usage") val usage: Map<String, Any>
)

data class ChoiceItem(
    @field:JsonProperty("message") val message: Message,
    @field:JsonProperty("index") val index: Int,
    @field:JsonProperty("finish_reason") val finishReason: String
)



