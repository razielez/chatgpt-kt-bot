package com.razielez.chatgpt.app.infra.gpt

interface ChatLoadBalance {
    fun get(): Token
}