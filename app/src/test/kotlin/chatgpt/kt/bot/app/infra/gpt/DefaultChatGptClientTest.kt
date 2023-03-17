package chatgpt.kt.bot.app.gpt

import chatgpt.kt.bot.app.infra.gpt.*
import chatgpt.kt.bot.app.infra.utils.JsonTools
import com.fasterxml.jackson.databind.ObjectMapper
import okio.buffer
import okio.source
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class DefaultChatGptClientTest : BaseChatgptTest() {


    private lateinit var client: DefaultChatGptClient

    @BeforeEach
    fun setUp() {
        client = initClient()
    }

    @Test
    fun test_json() {
        val mapper = ObjectMapper()
        val q = CompletionReq(
            model = "",
            message = listOf(Message("user", "content")),
            maxToken = 10,
            temperature = 0.5f,
            topP = 1
        )
        var s = mapper.writeValueAsString(q)
        println("Json: \n$s")
        s = """
            {"id":"chatcmpl-6qiMyIjdiJFQn2AtV6B2ww4seuyk5","object":"chat.completion","created":1678021632,"model":"gpt-3.5-turbo-0301","usage":{"prompt_tokens":29,"completion_tokens":171,"total_tokens":200},"choices":[{"message":{"role":"assistant","content":"\n\n抱歉，作为AI语言模型，我并不知道关于Rust编程语言的笑话。但是，我可以告诉你一些有趣的事实。Rust是一种由Mozilla开发的系统编程语言，其设计目标是安全、并发和高效。Rust在2010年首次亮相，自那以后已经成为一个受欢迎的编程语言。它的语法类似于C++，但它有一些独特的特性，例如所有权模型和借用检查器，可以帮助程序员避免一些常见的错误。"},"finish_reason":"stop","index":0}]}
        """.trimIndent()
        val c = JsonTools.fromJson(s, CompletionResp::class.java)
        println(c)
    }

    @Test
    fun test_completions() {
        val qs = listOf(
            Message("user", "hello, please use java to write a generate random number program!"),
            Message("user", "then, rewrite it in kotlin!"),
            Message("user", "then, rewrite it in rust!"),
        )
        val ctx = mutableListOf<Message>()
        qs.forEach {
            ctx.add(it)
            val m = client.completions(ctx)
            println(m)
        }
    }

    @Test
    fun `test parser`() {
        val s = """
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"role":"assistant"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":"\n\n"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":"Why"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" was"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" the"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" math"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" book"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" sad"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":"?"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" It"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" had"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" too"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" many"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":" problems"},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{"content":"."},"index":0,"finish_reason":null}]}
            data: {"id":"chatcmpl-6v0JIkqwGwBdjrOXXK7NoaWG64EHL","object":"chat.completion.chunk","created":1679043908,"model":"gpt-3.5-turbo-0301","choices":[{"delta":{},"index":0,"finish_reason":"stop"}]}
            data: [DONE]
        """.trimIndent()
        val ins = s.byteInputStream()
        val buffer = ins.source().buffer()
        var line: String?
        val prefix = "data: "
        var answer = ""
        while (buffer.readUtf8Line().also { line = it } != null) {
            if (line.isNullOrEmpty() || !line!!.startsWith(prefix)) {
                break
            }
            val t = line!!.substring(prefix.length)
            if (!t.startsWith("{")) {
                break
            }
            val resp = JsonTools.fromJson(t, CompletionResp::class.java)
            val delta = resp.choices[0].delta
            delta?.content?.also {
                answer += it
            }
        }
        println("The answer: $answer")
    }

    @Test
    fun `test completions seq`() {
        var answer = ""
        client.completionsSeq(listOf(Message("user", "please tell me a joke?"))).iterator()
            .forEach { resp ->
                println(resp)
                resp.choices[0].delta?.content?.also {
                    answer += it
                }
            }
        println("The answer is: $answer")
    }
}