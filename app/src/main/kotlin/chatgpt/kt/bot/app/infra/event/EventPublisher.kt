package chatgpt.kt.bot.app.infra.event

interface EventPublisher<T> {

    fun send(event: T)
}