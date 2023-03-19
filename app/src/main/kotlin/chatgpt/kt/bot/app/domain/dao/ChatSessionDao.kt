package chatgpt.kt.bot.app.domain.dao

import chatgpt.kt.bot.app.domain.ChatSession
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.Role

interface ChatSessionDao {

    fun save(session: ChatSession)

    fun byId(id: String): ChatSession

    fun clear(id: String): ChatSession?

    fun with(sessionId: String, q: String, role: Role = Role.USER, func: (m: List<Message>) -> Message): Pair<Message, ChatSession> {
        val session = byId(sessionId)
        session.append(Message(role.value, q))
        val result = try {
            func.invoke(session.messages)
        } catch (e: Exception) {
            session.messages.removeLast()
            throw e
        }
        session.append(result)
        save(session)
        return Pair(result, session)
    }

}