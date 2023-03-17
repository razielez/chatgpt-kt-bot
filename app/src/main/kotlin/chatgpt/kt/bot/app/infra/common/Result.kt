package chatgpt.kt.bot.app.infra.common

sealed class Result<T>
data class Error<T>(val error: Throwable) : Result<T>()
data class Value<T>(val value: T) : Result<T>()

inline fun <T> mightFail(scope: () -> T): Result<T> {
    return try {
        Value(scope())
    } catch (ex: Throwable) {
        Error(ex)
    }
}

fun <T> notNull(value: T?) = mightFail {
    value!!
}

fun printVal(value: Result<String>) {
    when (value) {
        is Error -> println("error: ${value.error}")
        is Value -> println("value: ${value.value}")
    }
}