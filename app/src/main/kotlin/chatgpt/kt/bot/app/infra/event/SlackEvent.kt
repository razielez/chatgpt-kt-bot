package chatgpt.kt.bot.app.infra.event

data class SlackEvent(
    val responseUrl: String,
    val msg: String,
)