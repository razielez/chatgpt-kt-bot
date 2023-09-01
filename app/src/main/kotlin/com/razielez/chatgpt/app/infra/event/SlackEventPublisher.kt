package com.razielez.chatgpt.app.infra.event

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

open class SlackEventPublisher(
    private val channel: Channel<SlackEvent>
) : EventPublisher<SlackEvent> {

    @OptIn(DelicateCoroutinesApi::class)
    override fun send(event: SlackEvent) {
        GlobalScope.launch {
            kotlin.run {
                channel.send(event)
            }
        }
    }
}