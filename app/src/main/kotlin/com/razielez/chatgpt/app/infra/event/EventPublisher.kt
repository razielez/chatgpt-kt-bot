package com.razielez.chatgpt.app.infra.event

interface EventPublisher<T> {

    fun send(event: T)
}