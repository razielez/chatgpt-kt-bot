package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.event.handler.Kind
import org.junit.jupiter.api.Test

class SlackCommandEventTest {

    @Test
    fun `test_paser`() {
        val msg = "/sys 测试一下"
        val kind = Kind.parse(msg)
        val event = SlackChatEvent("", msg, kind, "", "")
        println(event)
        println(event.parsedMsg())
    }
}