package com.razielez.chatgpt.app.infra.utils

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.random.Random

object Retry {
    private val log = KotlinLogging.logger {  }
    suspend fun <T> withBackoff(
        maxRetries: Int = 5,
        initialDelayMillis: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelayMillis = initialDelayMillis
        repeat(maxRetries) { retryCount ->
            try {
                return block()
            } catch (e: Exception) {
                if (retryCount == maxRetries - 1) {
                    log.error { "exceed retry count, at: $retryCount" }
                    throw e
                } else {
                    val randomMs = Random.nextLong(currentDelayMillis / 2, currentDelayMillis)
                    delay(randomMs)
                    currentDelayMillis = (currentDelayMillis * factor).toLong()
                }
            }
        }
        throw IllegalStateException("Retry failed! max: $maxRetries")
    }

}