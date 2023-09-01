package com.razielez.chatgpt.app.infra.gpt

class ChatGptException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}