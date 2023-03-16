package chatgpt.kt.bot.app.infra.gpt

import chatgpt.kt.bot.app.infra.common.Serializable
import chatgpt.kt.bot.app.infra.utils.JsonTools
import com.fasterxml.jackson.annotation.JsonProperty

interface Gpt3Client {

    fun completions(message: List<Message>): Message

}


data class Message(
    @JsonProperty("role") val role: String,
    @JsonProperty("content") val content: String
) : Serializable

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
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val objectStr: String,
    @JsonProperty("created") val createTs: Int,
    @JsonProperty("model") val model: String,
    @JsonProperty("choices") val choices: List<ChoiceItem>,
    @JsonProperty("usage") val usage: Map<String, Any>
) : Serializable

data class ChoiceItem(
    @JsonProperty("message") val message: Message,
    @JsonProperty("index") val index: Int,
    @JsonProperty("finish_reason") val finishReason: String?
) : Serializable



