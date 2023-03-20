package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackCommandEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.Role
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
open class TranslateHandler(
    @Qualifier("slackBaseImpl") private val slackBase: SlackBase,
    @Qualifier("chatBaseImpl") private val chatBase: ChatBase
) : Handler, SlackBase by slackBase, ChatBase by chatBase {

    private val log = KotlinLogging.logger { }

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackCommandEvent
        val q = se.parsedMsg().trim()
        val sys = Message(Role.SYSTEM.value, "You are a translation engine that can only translate text and cannot interpret it.")
        val action = TranslateAction.of(q)
        if (action.isUnknown()) {
            log.warn { "translate parse failed! q: $q" }
            return true
        }
        val user = Message(Role.USER.value, "translate it from ${action.from.first.value} to ${action.to.first.value}, 这句话是 ${action.text}")
        val ans = completions(
            listOf(
                sys,
                user
            )
        )
        sendByCmd(se.responseUrl, ans)
        return true
    }


    override fun kind() = Kind.TRANSLATE

}

data class TranslateAction(
    val from: Pair<Lang, Double>,
    val to: Pair<Lang, Double>,
    val text: String
) {

    fun isUnknown() = from.first == Lang.UNKNOWN || to.first == Lang.UNKNOWN

    companion object {
        fun unknown() = TranslateAction(Pair(Lang.UNKNOWN, Double.MIN_VALUE), Pair(Lang.UNKNOWN, Double.MIN_VALUE), "")

        fun of(text: String): TranslateAction {
            if (text.isBlank()) return unknown()
            val len = text.length
            val counts = text.map { Lang.of(it) }
                .filter { it != Lang.UNKNOWN }
                .groupingBy { it }
                .eachCount()
            val result = counts.mapValues { it.value.toDouble() / len }
                .entries
                .sortedByDescending { it.value }
            if (result.size < 2) return unknown()
            return TranslateAction(
                from = Pair(result[0].key, result[0].value),
                to = Pair(result[1].key, result[1].value),
                text = text.trim()
            )
        }

    }
}


enum class Lang(val value: String, val description: String, val range: IntRange) {
    ZH_HANS("zh-Hans", "简体中文", 0x4E00..0x9FA5),
    EN("en", "English", 0x0000..0x007F),
    UNKNOWN("", "", IntRange.EMPTY)
    ;

    companion object {
        fun of(ch: Char): Lang = values().firstOrNull { it.range.contains(ch.code) } ?: UNKNOWN

    }
}
