package com.razielez.chatgpt.app.ui.http

import com.slack.api.bolt.App
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet
import jakarta.servlet.annotation.WebServlet
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@WebServlet("/slack/events")
open class SlackApi(app: App) : SlackAppServlet(app) {

}