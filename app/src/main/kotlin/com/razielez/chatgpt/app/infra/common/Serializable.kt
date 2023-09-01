package com.razielez.chatgpt.app.infra.common

import com.razielez.chatgpt.app.infra.utils.JsonTools

interface Serializable


fun Serializable.toJson(): String = JsonTools.toJson(this)

fun Collection<Serializable>.toJson(): String = JsonTools.toJson(this)
