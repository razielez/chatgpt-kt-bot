package chatgpt.kt.bot.app.infra.persistence

import chatgpt.kt.bot.app.infra.gpt.BaseChatgptTest
import chatgpt.kt.bot.app.infra.gpt.DefaultChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ChatSessionDaoImplTest : BaseChatgptTest() {

    private val sessionDao = ChatSessionDaoImpl("/tmp")

    private lateinit var client: DefaultChatGptClient

    @BeforeEach
    fun setUp() {
        client = initClient()
    }

    @Test
    fun `test cache`() {
        val qs = listOf(
            Message("user", "你好,可以讲一个笑话吗"),
            Message("user", "可以把这个笑话翻译成英文吗?"),
            Message("user", "可以用中文更详细的表述下吗?"),
        )
        qs.forEach { it ->
            println("Q: ${it.content}")
            val (m,_) = sessionDao.with("test", it.content, Role.USER){ client.completions(it)}
            println("A: ${m.content}")
        }
    }
}