package chatgpt.kt.bot.app.infra.event

import chatgpt.kt.bot.app.infra.event.handler.Handler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
open class SlackEventConsumer(
    private val channel: Channel<SlackEvent>,
    private val handlers: List<Handler>
) : EventConsumer, InitializingBean {


    private val log = KotlinLogging.logger { }

    @OptIn(DelicateCoroutinesApi::class)
    override fun receive() {
        GlobalScope.launch {
            while (true) {
                val event = channel.receive()
                if (log.isDebugEnabled) {
                    log.debug { "Receive: $event, msg: [${event.msg()}]" }
                }
                GlobalScope.launch {
                    try {
                        Handler.lookup(event.kind(), handlers).hande(event)
                    } catch (e: Throwable) {
                        log.error { "catch e, msg:${e.message}, ${e.printStackTrace()}" }
                    }
                }
            }
        }
    }


    override fun afterPropertiesSet() {
        receive()
    }

}