package chatgpt.kt.bot.app.common

import chatgpt.kt.bot.app.utils.JsonTools

interface Serializable


fun Serializable.toJson(): String = JsonTools.toJson(this)

fun Collection<Serializable>.toJson(): String = JsonTools.toJson(this)
