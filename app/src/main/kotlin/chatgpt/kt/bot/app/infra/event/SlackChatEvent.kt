package chatgpt.kt.bot.app.infra.event

data class SlackChatEvent(
    val from: String,
    val msg: String,
    val channel: String,
    val ts: String,
    val token:String,
) : SlackEvent {
    override fun msg(): String  = msg
}