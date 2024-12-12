package com.chess.puzzle.text2sql.web.utility

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

/**
 * Executes a block of code with a temporarily modified log level.
 *
 * This utility function allows you to change the root logger's log level for the duration of the
 * provided block. After the block is executed, the original log level is restored.
 *
 * @param level The log level to set temporarily while executing the block.
 * @param block The block of code to execute with the modified log level.
 * @return The result of the block of code.
 * @sample
 *
 * ```kotlin
 * withLogLevel(Level.DEBUG) {
 *     logger.debug("This is a debug message")
 * }
 * ```
 * ```
 * var foo = withLogLevel(Level.OFF) {
 *   getFoo()
 * }
 * ```
 */
// Note: This function modifies the root logger's level, which is a global state.
// Ensure that this function is used in a thread-safe manner if called concurrently.
inline fun <R> withLogLevel(level: Level, block: () -> R): R {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    val originalLevel = rootLogger.level
    return try {
        rootLogger.level = level
        block()
    } finally {
        rootLogger.level = originalLevel
    }
}
