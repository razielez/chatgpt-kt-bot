package chatgpt.kt.bot.app.infra.event

data class SlackCommandEvent(
    val responseUrl: String,
    val msg: String,
) : SlackEvent {
    override fun msg(): String = msg
}