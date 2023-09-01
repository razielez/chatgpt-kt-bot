package chatgpt.kt.bot.app.infra.gpt

import com.razielez.chatgpt.app.infra.gpt.ChatGptProperties
import com.razielez.chatgpt.app.infra.gpt.ChatLoadBalanceImpl
import com.razielez.chatgpt.app.infra.gpt.ChatgptCfg
import com.razielez.chatgpt.app.infra.gpt.DefaultChatGptClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(value = [ChatGptProperties::class])
@TestPropertySource(
    "classpath:application.properties",
    "classpath:application-dev.properties",
)
open class BaseChatgptTest {

    @Autowired
    private lateinit var properties: ChatGptProperties
    fun initClient(): DefaultChatGptClient {
        return DefaultChatGptClient(
            properties, ChatgptCfg().chatgptOkHttpClient(properties), ChatLoadBalanceImpl(properties)
        )
    }

    @Test
    fun `test properties`() {
        println(properties)
    }

}