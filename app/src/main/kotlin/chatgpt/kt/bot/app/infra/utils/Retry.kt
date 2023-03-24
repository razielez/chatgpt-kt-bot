package chatgpt.kt.bot.app.infra.utils

import kotlinx.coroutines.delay
import kotlin.random.Random

object Retry {
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
                    throw e
                } else {
                    val randomMs = Random.nextLong(currentDelayMillis / 2, currentDelayMillis)
                    delay(randomMs)
                    currentDelayMillis = (currentDelayMillis * factor).toLong()
                }
            }
        }
        throw IllegalStateException("Retry failed!")
    }

}