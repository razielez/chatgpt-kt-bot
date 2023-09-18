package com.razielez.chatgpt.app.infra.common

import com.razielez.chatgpt.app.infra.utils.JsonTools

interface InternalSerializable


fun InternalSerializable.toJson(): String = JsonTools.toJson(this)

fun Collection<InternalSerializable>.toJson(): String = JsonTools.toJson(this)
