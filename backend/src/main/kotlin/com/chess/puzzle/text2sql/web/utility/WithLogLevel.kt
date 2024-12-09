package com.chess.puzzle.text2sql.web.utility

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

inline fun withLogLevel(level: Level, block: () -> Unit) {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    val originalLevel = rootLogger.level
    try {
        rootLogger.level = level
        block()
    } finally {
        rootLogger.level = originalLevel
    }
}
