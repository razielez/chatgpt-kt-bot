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
        val user = Message(Role.USER.value, "translate it from ${action.from.value} to ${action.to.value}, {${action.text}}")
        val ans = completions(
            listOf(
                sys,
                user
            )
        )
        val reply = "Q: ${action.text} \nA: $ans"
        sendByCmd(se.responseUrl, reply)
        return true
    }


    override fun kind() = Kind.TRANSLATE

}

data class TranslateAction(
    val from: Lang,
    val to: Lang,
    val text: String
) {

    fun isUnknown() = from == Lang.UNKNOWN || to == Lang.UNKNOWN

    companion object {
        fun unknown() = TranslateAction(Lang.UNKNOWN, Lang.UNKNOWN, "")

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

            if (result.isEmpty()) return unknown()
            val from = result[0].key
            val to = if (result.size == 1) {
                when (from) {
                    Lang.EN -> Lang.ZH_HANS
                    Lang.ZH_HANS -> Lang.EN
                    else -> Lang.UNKNOWN
                }
            } else {
                result[1].key
            }

            return TranslateAction(
                from = from,
                to = to,
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
