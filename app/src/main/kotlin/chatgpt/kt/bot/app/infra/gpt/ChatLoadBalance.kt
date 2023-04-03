package chatgpt.kt.bot.app.infra.gpt

interface ChatLoadBalance {
    fun get(): Token
}