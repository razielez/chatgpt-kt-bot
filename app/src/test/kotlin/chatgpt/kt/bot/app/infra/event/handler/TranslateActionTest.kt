package chatgpt.kt.bot.app.infra.event.handler

import com.razielez.chatgpt.app.infra.event.handler.TranslateAction
import org.junit.jupiter.api.Test

class TranslateActionTest {

    @Test
    fun `test of`() {
        val action = TranslateAction.of("这是一段中文的描述,统计下word含量,English")
        println(action)
    }
}