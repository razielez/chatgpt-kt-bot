package com.razielez.chatgpt.app.infra

import com.razielez.chatgpt.app.infra.event.SlackCommandEvent
import com.razielez.chatgpt.app.infra.event.SlackEvent
import com.razielez.chatgpt.app.infra.event.SlackEventPublisher
import com.razielez.chatgpt.app.infra.gpt.ChatGptProperties
import kotlinx.coroutines.channels.Channel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ContextCfg {


    @Bean
    open fun channel() = Channel<SlackEvent>()

    @Bean
    open fun eventPublisher(channel: Channel<SlackEvent>) = SlackEventPublisher(channel)

}