package chatgpt.kt.bot.app.infra.event.handler

sealed interface Handler {
    fun hande(event: Event): Boolean
    fun kind(): Kind
    fun match(msg: String) = kind() == Kind.match(msg)
}

data class Event(
    val msgId: String,
    val msg: String,
    val email: String,
    val ts: Long
)

enum class Kind(
    val prefix: String?
) {
    CHAT(""),
    CLEAR("/clear"),
    EMPTY(null)
    ;

    companion object {
        fun match(msg: String?): Kind = if (msg.isNullOrBlank()) {
            EMPTY
        } else {
            values().first { it.prefix == msg }
        }
    }
}

