package chatgpt.kt.bot.app.infra.event

data class SlackChatEvent(
    val from: String,
    val msg: String,
    val channel: String,
    val ts: String
) : SlackEvent