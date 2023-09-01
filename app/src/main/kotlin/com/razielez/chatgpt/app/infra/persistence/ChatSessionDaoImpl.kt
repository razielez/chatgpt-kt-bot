package com.razielez.chatgpt.app.infra.persistence

import com.razielez.chatgpt.app.domain.ChatSession
import com.razielez.chatgpt.app.domain.dao.ChatSessionDao
import com.razielez.chatgpt.app.infra.common.toJson
import com.razielez.chatgpt.app.infra.utils.JsonTools
import mu.KotlinLogging
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
    @Value("\${chatgpt.session.dir}") val sessionPath: String,
) : ChatSessionDao, InitializingBean, DisposableBean {

    private val map = ConcurrentHashMap<String, ChatSession>(5)

    private val log = KotlinLogging.logger { }

    private lateinit var persisPath: Path


    override fun save(session: ChatSession) {
        map[session.sessionId] = session
    }

    override fun byId(id: String): ChatSession {
        return map[id] ?: ChatSession.of(id)
    }

    override fun all(): List<ChatSession> {
        return map.values.toList()
    }

    override fun clear(id: String): ChatSession? {
        log.info { "remove session $id" }
        return map.remove(id)
    }

    override fun afterPropertiesSet() {
        registerLoadSession()
    }

    private fun registerLoadSession() {
        persisPath = Path(sessionPath, "session.json")
        if (Files.exists(persisPath)) {
            Files.lines(persisPath).forEach {
                val session = JsonTools.fromJson(it, ChatSession::class.java)
                map[session.sessionId] = session
            }
            log.info { "load session size: ${map.size}" }
        }
    }

    override fun destroy() {
        registerPersisSession()
    }

    private fun registerPersisSession() {
        val lines = map.values.map { it.toJson() }
        Files.write(persisPath, lines, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
        log.info { "persis session size: ${lines.size}" }
    }
}