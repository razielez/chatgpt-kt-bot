package com.razielez.chatgpt.app.infra.event.handler

import com.razielez.chatgpt.app.domain.dao.ChatSessionDao
import com.razielez.chatgpt.app.infra.event.SlackEvent
import com.razielez.chatgpt.app.infra.gpt.ChatGptClient
import com.razielez.chatgpt.app.infra.gpt.Message
import com.razielez.chatgpt.app.infra.gpt.Role
import com.razielez.chatgpt.app.infra.slack.SlackProperties
import com.slack.api.Slack
import com.slack.api.app_backend.slash_commands.SlashCommandResponseSender
import com.slack.api.app_backend.slash_commands.response.SlashCommandResponse
import com.slack.api.bolt.App
import com.slack.api.methods.request.chat.ChatUpdateRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.model.User
import com.slack.api.rate_limits.RateLimiter
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

sealed interface Handler {
    fun hande(event: SlackEvent): Boolean
    fun kind(): Kind

    companion object {
        fun lookup(kind: Kind, handlers: List<Handler>): Handler = handlers.first { it.kind() == kind }
    }
}

interface ChatBase {

    fun completions(sessionId: String, q: String, role: Role = Role.USER): String

    fun completionsSSE(sessionId: String, q: String, role: Role = Role.USER, sender: (text: String, idx: Int) -> Unit)

    fun completions(messages: List<Message>): String

}

@Component
class ChatBaseImpl(
    private val chatSessionDao: ChatSessionDao,
    private val chatGptClient: ChatGptClient,
) : ChatBase {

    private val log = KotlinLogging.logger { }
    override fun completions(sessionId: String, q: String, role: Role): String {
        return try {
            val (m, _) = chatSessionDao.with(
                sessionId,
                q,
                role
            ) {
                chatGptClient.completions(it)
            }
            m.content
        } catch (e: Exception) {
            log.error { "request gpt failed! ${e.printStackTrace()}" }
            DEFAULT_MSG
        }
    }

    override fun completions(messages: List<Message>): String {
        return try {
            chatGptClient.completions(messages).content
        } catch (e: Exception) {
            log.error { "completions error $e" }
            DEFAULT_MSG
        }
    }

    override fun completionsSSE(sessionId: String, q: String, role: Role, sender: (text: String, idx: Int) -> Unit) {
        try {
            chatSessionDao.with(
                sessionId,
                q,
                role
            ) { it ->
                val seq = chatGptClient.completionsSSE(it)
                var content = ""
                var count = 0
                seq.iterator().forEach { resp ->
                    resp.choices[0].delta?.content?.also {
                        content += it
                        sender.invoke(content, count++)
                    }
                }
                Message(role.value, content)
            }

        } catch (e: Exception) {
            log.error { "request gpt failed! $e" }
        }
    }


    companion object {
        const val DEFAULT_MSG = "机器人开始摆烂..."
    }

}

interface SlackBase {

    fun findUser(user: String): User

    fun send(channel: String, text: String)

    fun edit(channel: String, text: String, ts: String?): String

    fun sendByCmd(responseUrl: String, reply: String)

}

@Component
class SlackBaseImpl(
    private val app: App,
    private val slackProperties: SlackProperties,
) : SlackBase {
    private val slack = Slack.getInstance()
    private val responder = SlashCommandResponseSender(slack)
    private val rateLimiter = com.google.common.util.concurrent.RateLimiter.create(50.0, 1, TimeUnit.MINUTES)


    private val log = KotlinLogging.logger { }
    override fun findUser(user: String): User {
        val uiq = UsersInfoRequest.builder()
            .token(slackProperties.botToken)
            .user(user)
            .build()
        return app.client.usersInfo(uiq).user
    }

    override fun send(channel: String, text: String) {
        val response = app.client.chatPostMessage { r -> r.channel(channel).text(text) }
        if (!response.isOk) {
            log.error { "chat.PostMsg failed: $channel, $text, ${response.error}" }
        }
    }

    override fun edit(channel: String, text: String, ts: String?): String {
        rateLimiter.acquire()
        val t = text.ifBlank { " " }
        return if (ts == null) {
            val response = app.client.chatPostMessage { r -> r.channel(channel).text(t) }
            if (!response.isOk) {
                log.error { "chat.PostMsg failed: $channel, $t, ${response.error}" }
                throw RuntimeException("chat.PostMsg failed: $channel, $t, ${response.error}")
            }
            response.ts
        } else {
            val q = ChatUpdateRequest.builder()
                .channel(channel)
                .text(t)
                .ts(ts)
                .build()
            val resp = app.client.chatUpdate(q)
            if (!resp.isOk) {
                log.error { "chat.Update failed: $channel, $t, ${resp.error}" }
            }
            resp.ts
        }

    }

    override fun sendByCmd(responseUrl: String, reply: String) {
        val response = responder.send(
            responseUrl,
            SlashCommandResponse.builder()
                .text(reply)
                .build()
        )
        if (!IntRange(200, 299).contains(response.code)) {
            log.error { "Webhook response failed! reply: $reply msg: ${response.message}, resp: $response" }
        }
    }
}

enum class Kind(
    val prefix: String,
    val description: String,
    val isSlackCmd: Boolean,
    val shouldDisplay: Boolean,
) {
    ASK("/ask", "不包含上下文的提问", true, true),
    CHAT("", "包含上下文的对话", false, false),
    CLEAR("/clear", "清空对话上下文", false, true),
    SYS_ROLE("/sys", "引入设定,角色扮演", false, true),
    TRANSLATE("/translate", "翻译", true, true),
    EMPTY("", "", false, false),
    HELP("/help", "help", false, true);

    companion object {
        fun parse(msg: String?): Kind = if (msg.isNullOrBlank()) {
            EMPTY
        } else {
            values().filter { it.prefix.isNotBlank() }.find { msg.startsWith(it.prefix) } ?: CHAT
        }

        fun display(): String {
            val c = values()
                .filter { it.shouldDisplay }
                .joinToString("\n") {
                    "Command:${it.prefix}, Description: ${it.description}"
                }
            return "This is the command supported by this plugin\n$c"
        }
    }
}

