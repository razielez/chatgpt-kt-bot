package chatgpt.kt.bot.app.gpt

import chatgpt.kt.bot.app.common.toJson
import chatgpt.kt.bot.app.utils.JsonTools
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.time.Duration

open class DefaultGptClient(
    private val properties: ChatGptPropertiesProvider
) : Gpt3Client {

    private var client: OkHttpClient


    private val log = KotlinLogging.logger {}

    init {
        val b = OkHttpClient().newBuilder()
            .callTimeout(Duration.ofMinutes(3))
            .readTimeout(Duration.ofMinutes(3))
            .writeTimeout(Duration.ofMinutes(3))
            .connectTimeout(Duration.ofMinutes(3))
        if (InetAddress.getLocalHost().hostAddress.startsWith("192.168")) {
            b.proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", 1089)))
        }
        client = b.build()
    }

    override fun completions(message: List<Message>): Message {
        val token = properties.properties().token
        val q = Request.Builder()
            .url("$BASE_URL/chat/completions")
            .post(buildPostBody(message).toRequestBody())
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $token")
            .build()
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

    private fun buildPostBody(message: List<Message>): String {
        return CompletionReq(
            model = ENGINE,
            message = message,
            maxToken = MAX_TOKEN,
            temperature = TEMPERATURE,
        ).toJson()
    }

    companion object {
        const val BASE_URL = "https://api.openai.com/v1"
        const val MAX_TOKEN = 2000
        const val TEMPERATURE = 0.7f
        const val ENGINE = "gpt-3.5-turbo"
    }

}