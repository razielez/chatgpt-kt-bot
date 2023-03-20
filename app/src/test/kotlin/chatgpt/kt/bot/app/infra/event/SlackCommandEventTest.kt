package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.event.handler.Kind
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SlackCommandEventTest{

    @Test
    fun `test_paser`() {
        val msg = "/sys 假装"
        val event = SlackChatEvent("", msg, Kind.parse(msg), "", "", "")
        println(event.parsedMsg())
    }
}