package chatgpt.kt.bot.app.gpt

class ChatGptEnvProvider : ChatGptPropertiesProvider {
    override fun properties(): ChatGptProperties {
        return ChatGptProperties(System.getenv("OPENAI_TOKEN")!!)
    }
}