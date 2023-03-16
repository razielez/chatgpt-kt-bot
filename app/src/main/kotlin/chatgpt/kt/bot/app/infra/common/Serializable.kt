package chatgpt.kt.bot.app.infra.common

import chatgpt.kt.bot.app.infra.utils.JsonTools

interface Serializable


fun Serializable.toJson(): String = JsonTools.toJson(this)

fun Collection<Serializable>.toJson(): String = JsonTools.toJson(this)
