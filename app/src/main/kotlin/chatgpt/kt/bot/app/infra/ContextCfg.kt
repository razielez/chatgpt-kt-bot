package chatgpt.kt.bot.app.infra

import chatgpt.kt.bot.app.infra.event.SlackCommandEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.event.SlackEventPublisher
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