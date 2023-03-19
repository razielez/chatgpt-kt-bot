package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.domain.dao.ChatSessionDao
import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.slack.SlackProperties
import com.slack.api.bolt.App
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.model.User
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
open class ClearHandler(
    private val chatSessionDao: ChatSessionDao,
    private val slackProperties: SlackProperties,
    private val app: App
) : Handler {

    private val log = KotlinLogging.logger {}

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val sessionId = findUser(se.from).profile.email
        chatSessionDao.clear(sessionId)
        val response = app.client.chatPostMessage { r -> r.channel(se.channel).text("clean done!") }
        if (!response.isOk) {
            log.error { "chat.PostMsg failed: ${response.error}" }
        }
        return true
    }

    private fun findUser(user: String): User {
        val uiq = UsersInfoRequest.builder()
            .token(slackProperties.botToken)
            .user(user)
            .build()
        return app.client.usersInfo(uiq).user
    }

    override fun kind() = Kind.CLEAR
}