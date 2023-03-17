package chatgpt.kt.bot.app.infra.gpt

import chatgpt.kt.bot.app.infra.common.toJson
import chatgpt.kt.bot.app.infra.utils.JsonTools
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.buffer
import okio.source
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
open class DefaultChatGptClient(
    private val properties: ChatGptProperties,
    @Qualifier("chatgptOkHttpClient") private val client: OkHttpClient,
) : ChatGptClient {

    private val log = KotlinLogging.logger {}

    override fun completions(message: List<Message>): Message {
        val q = buildRequest(buildPostBody(message), properties.token)
        val now = System.currentTimeMillis()
        val response = client.newCall(q).execute()
        if (!response.isSuccessful) {
            log.error { "request failed! req: ${message.toJson()}, response: ${response.body?.string() ?: "is null"}" }
            throw ChatGptException("request failed!")
        }
        val body = response.body?.string()
        log.info { "req: ${message.toJson()}, resp: ${body}, cost: ${System.currentTimeMillis() - now} ms" }
        return body?.let { JsonTools.fromJson(it, CompletionResp::class.java).choices[0].message } ?: throw ChatGptException("response is null!")
    }

    override fun completionsSeq(message: List<Message>): Sequence<CompletionResp> = sequence {
        val q = buildRequest(buildStreamBody(message), properties.token)
        val response = client.newCall(q).execute()
        if (!response.isSuccessful) {
            log.error { "request failed! req: ${message.toJson()}, response: ${response.body?.string() ?: " is null"}" }
            throw ChatGptException("request failed!")
        }
        var line: String?
        val buffer = response.body?.byteStream()?.source()?.buffer()
        while (buffer?.readUtf8Line().also { line = it } != null) {
            if (line.isNullOrBlank()) {
                continue
            }
            if (!line!!.startsWith(DELTA_PREFIX)) {
                log.warn { "Unexpected line: $line " }
                break
            }
            val t = line!!.substring(DELTA_PREFIX.length)
            if (t.endsWith(DELTA_END_SUFFIX)) {
                break
            }
            if (!t.startsWith("{")) {
                log.warn { "end with not a json: $line" }
                break
            }
            val resp = JsonTools.fromJson(t, CompletionResp::class.java)
            yield(resp)
        }
    }

    private fun buildStreamBody(message: List<Message>): String {
        return CompletionReq(
            model = Model.GPT3_5_TURBO.value,
            message = message,
            maxToken = MAX_TOKEN,
            temperature = TEMPERATURE,
            stream = true
        ).toJson()
    }


    private fun buildRequest(body: String, token: String): Request {
        return Request.Builder()
            .url("$BASE_URL/chat/completions")
            .post(body.toRequestBody())
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $token")
            .build()
    }

    private fun buildPostBody(message: List<Message>): String {
        return CompletionReq(
            model = Model.GPT3_5_TURBO.value,
            message = message,
            maxToken = MAX_TOKEN,
            temperature = TEMPERATURE,
        ).toJson()
    }

    companion object {
        const val BASE_URL = "https://api.openai.com/v1"
        const val MAX_TOKEN = 2000
        const val TEMPERATURE = 0.7f
        const val DELTA_PREFIX = "data: "
        const val DELTA_END_SUFFIX = "data: "
    }

}