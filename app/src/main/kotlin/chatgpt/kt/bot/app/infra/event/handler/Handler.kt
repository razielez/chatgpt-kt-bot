package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackEvent

sealed interface Handler {
    fun hande(event: SlackEvent): Boolean
    fun kind(): Kind

    companion object {
        fun lookup(kind: Kind, handlers: List<Handler>): Handler = handlers.first { it.kind() == kind }
    }
}

enum class Kind(
    val prefix: String,
) {
    ASK("/ask"), // 不包含上下文
    CHAT(""), // 上下文
    CLEAR("/clear"), // 清空上下文
    SYS_ROLE("/sys"), // 引入设定 角色扮演
    TRANSLATE("/translate"),
    EMPTY(""),
    ;

    companion object {
        fun parse(msg: String?): Kind = if (msg.isNullOrBlank()) {
            EMPTY
        } else {
            values().firstOrNull { it.prefix == msg } ?: CHAT
        }
    }
}

