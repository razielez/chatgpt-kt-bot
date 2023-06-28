package chatgpt.kt.bot.app.infra.gpt

import chatgpt.kt.bot.app.infra.common.toJson
import chatgpt.kt.bot.app.infra.utils.JsonTools
import chatgpt.kt.bot.app.infra.utils.Retry
import kotlinx.coroutines.runBlocking
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
    private val chatGptProperties: ChatGptProperties,
    @Qualifier("chatgptOkHttpClient") private val client: OkHttpClient,
    private val chatLoadBalance: ChatLoadBalance,
) : ChatGptClient {

    private val log = KotlinLogging.logger {}

    override fun completions(message: List<Message>): Message {
        return runBlocking {
            Retry.withBackoff {
                val q = buildRequest(chatGptProperties.endpoint, buildPostBody(message), chatLoadBalance.get().v)
                val now = System.currentTimeMillis()
                val response = client.newCall(q).execute()
                response.use {
                    if (!response.isSuccessful) {
                        log.error { "request failed! req: ${message.toJson()}, response: ${response.body?.string() ?: "is null"}" }
                        throw ChatGptException("request failed!")
                    }
                    val body = response.body?.string()
                    log.info { "req: ${message.toJson()}, resp: ${body}, cost: ${System.currentTimeMillis() - now} ms" }
                    val result = body?.let { JsonTools.fromJson(it, CompletionResp::class.java).choices[0].message } ?: throw ChatGptException("response is null!")
                    log.info { "cost token: ${message.tokenLen()} result: $result" }
                    result
                }
            }
        }
    }

    override fun completionsSSE(message: List<Message>): Sequence<CompletionResp> = sequence {
        val q = buildRequest(chatGptProperties.endpoint, buildStreamBody(message), chatLoadBalance.get().v)
        val response = client.newCall(q).execute()
        response.use { it ->
            if (!response.isSuccessful) {
                log.error { "request failed! req: ${message.toJson()}, response: ${response.body?.string() ?: " is null"}" }
                throw ChatGptException("request failed!")
            }
            var line: String?
            val buffer = it.body?.byteStream()?.source()?.buffer()
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


    private fun buildRequest(endpoint: String, body: String, token: String): Request {
        return Request.Builder()
            .url("$endpoint/chat/completions")
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
        const val MAX_TOKEN = 2048
        const val TEMPERATURE = 0.7f
        const val DELTA_PREFIX = "data: "
        const val DELTA_END_SUFFIX = "[DONE]"
    }

}