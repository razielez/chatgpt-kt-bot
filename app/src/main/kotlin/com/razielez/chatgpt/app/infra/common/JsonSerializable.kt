package com.razielez.chatgpt.app.infra.common

import com.razielez.chatgpt.app.infra.utils.JsonTools

interface JsonSerializable


fun JsonSerializable.toJson(): String = JsonTools.toJson(this)

fun Collection<JsonSerializable>.toJson(): String = JsonTools.toJson(this)
