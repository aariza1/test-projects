package extensions

import mu.KLogger
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    println("Enabled: ${logger.isInfoEnabled}")
}

public fun KLogger.infoWithSingleSupplier(
    error: Throwable,
    arguments: Array<() -> Any>,
    message: () -> String,
) {
    if (isInfoEnabled) {
        info(
            message.invoke(),
            *arguments
                .map { it.invoke() }
                .toTypedArray() + error,
        )
    }
}

public fun KLogger.infoWithSingleSupplier(
    arguments: Array<() -> Any>,
    message: () -> String,
) {
    if (isInfoEnabled) {
        info(
            message.invoke(),
            *arguments
                .map { it.invoke() }
                .toTypedArray(),
        )
    }
}
