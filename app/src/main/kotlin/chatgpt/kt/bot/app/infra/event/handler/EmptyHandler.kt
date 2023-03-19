package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.infra.event.SlackEvent
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
open class EmptyHandler : Handler {

    private val log = KotlinLogging.logger {}

    override fun hande(event: SlackEvent): Boolean {
        log.info { "event is empty, ${event.msg()}" }
        return true
    }

    override fun kind() = Kind.EMPTY
}