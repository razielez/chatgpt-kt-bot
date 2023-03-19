package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.event.handler.Kind

data class SlackCommandEvent(
    val responseUrl: String,
    val msg: String,
    val kind: Kind
) : SlackEvent {
    override fun msg(): String = msg
    override fun kind() = kind
}