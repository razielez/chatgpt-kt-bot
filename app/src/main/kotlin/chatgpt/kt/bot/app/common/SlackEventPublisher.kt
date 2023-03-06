package chatgpt.kt.bot.app.common

import kotlinx.coroutines.channels.Channel

open class SlackEventPublisher(
    private val channel: Channel<SlackEvent>
) : EventPublisher<SlackEvent> {

    override fun send(event: SlackEvent) {

    }

}