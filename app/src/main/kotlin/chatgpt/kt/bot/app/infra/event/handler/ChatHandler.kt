package chatgpt.kt.bot.app.infra.event.handler

import chatgpt.kt.bot.app.domain.dao.ChatSessionDao
import chatgpt.kt.bot.app.infra.event.SlackChatEvent
import chatgpt.kt.bot.app.infra.event.SlackEvent
import chatgpt.kt.bot.app.infra.gpt.ChatGptClient
import chatgpt.kt.bot.app.infra.gpt.Message
import chatgpt.kt.bot.app.infra.gpt.Role
import chatgpt.kt.bot.app.infra.slack.SlackProperties
import com.slack.api.bolt.App
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.model.User
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
open class ChatHandler(
    private val chatGptClient: ChatGptClient,
    private val app: App,
    private val slackProperties: SlackProperties,
    private val chatSessionDao: ChatSessionDao,
) : Handler {

    private val log = KotlinLogging.logger {}

    override fun hande(event: SlackEvent): Boolean {
        val se = event as SlackChatEvent
        val user = findUser(se.from)
        val sessionId = user.profile.email
        val content = try {
            val (m, _) = chatSessionDao.with(
                sessionId,
                event.parsedMsg(),
            ) {
                Message.of(Role.ASSISTANT, chatGptClient.completionsSeq(it))
            }
            m.content
        } catch (e: Exception) {
            log.error { "request gpt failed! ${e.printStackTrace()}" }
            "开始摆烂..."
        }
        val response = app.client.chatPostMessage { r -> r.channel(se.channel).text(content) }
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

    override fun kind() = Kind.CHAT
}