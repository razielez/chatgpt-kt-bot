package chatgpt.kt.bot.app.common

data class SlackEvent(
    val responseUrl: String,
    val msg: String,
)