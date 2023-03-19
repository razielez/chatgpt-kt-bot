package chatgpt.kt.bot.app.infra.persistence

import chatgpt.kt.bot.app.domain.ChatSession
import chatgpt.kt.bot.app.domain.dao.ChatSessionDao
import chatgpt.kt.bot.app.infra.common.toJson
import chatgpt.kt.bot.app.infra.utils.JsonTools
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path

@Repository
open class ChatSessionDaoImpl(
    @Value("\${chatgpt.session.dir}") val sessionPath: String
) : ChatSessionDao, InitializingBean, DisposableBean {

    private val map = ConcurrentHashMap<String, ChatSession>(5)

    private lateinit var persisPath: Path


    override fun save(session: ChatSession) {
        map[session.sessionId] = session
    }

    override fun byId(id: String): ChatSession {
        return map[id] ?: ChatSession.of(id)
    }

    override fun clear(id: String): ChatSession? {
        return map.remove(id)
    }

    override fun afterPropertiesSet() {
        persisPath = Path(sessionPath, "session.json")
        if (Files.exists(persisPath)) {
            Files.lines(persisPath).forEach {
                val session = JsonTools.fromJson(it, ChatSession::class.java)
                map[session.sessionId] = session
            }
        }
    }

    override fun destroy() {
        if (Files.exists(persisPath)) {
            val lines = map.values.map { it.toJson() }
            Files.write(persisPath, lines, StandardOpenOption.APPEND)
        }
    }
}