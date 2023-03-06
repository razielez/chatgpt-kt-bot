package chatgpt.kt.bot.app.common

interface EventPublisher<T> {

    fun send(event: T)
}