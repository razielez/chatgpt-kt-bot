package chatgpt.kt.bot.app.infra.event

interface SlackEvent {

    fun msg(): String
}
