package com.razielez.chatgpt.app.infra.jobs

import com.razielez.chatgpt.app.domain.dao.ChatSessionDao
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
open class SessionExpireJob(
    private val chatSessionDao: ChatSessionDao,
) : InitializingBean, DisposableBean {

    private val log = KotlinLogging.logger { }

    private lateinit var t: Thread
    override fun afterPropertiesSet() {
        t = Thread {
            while (true) {
                TimeUnit.SECONDS.sleep(30)
                val now = System.currentTimeMillis()
                val sessions = chatSessionDao.all()
                sessions.forEach { it ->
                    it.removeExpire(now).also {
                        chatSessionDao.save(it)
                    }
                    log.info { "message size: ${it.messages.size} remove expire done!" }
                }
                log.info {
                    "session size: ${sessions.size} remove expire done!"
                }
            }
        }
        t.start()
    }

    override fun destroy() {
        t.interrupt()
    }
}