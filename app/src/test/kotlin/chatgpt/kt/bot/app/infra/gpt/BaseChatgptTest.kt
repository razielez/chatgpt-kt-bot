package chatgpt.kt.bot.app.infra.gpt

import chatgpt.kt.bot.app.infra.gpt.ChatGptProperties
import chatgpt.kt.bot.app.infra.gpt.ChatgptCfg
import chatgpt.kt.bot.app.infra.gpt.DefaultChatGptClient
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(value = [ChatGptProperties::class])
@TestPropertySource(
    "classpath:application-dev.properties",
)
open class BaseChatgptTest {

    @Autowired
    private lateinit var properties: ChatGptProperties

    fun initClient(): DefaultChatGptClient {
        return DefaultChatGptClient(
            properties, ChatgptCfg().chatgptOkHttpClient(properties)
        )

    }

}