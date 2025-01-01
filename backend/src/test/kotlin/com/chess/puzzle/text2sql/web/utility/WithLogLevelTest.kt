package com.chess.puzzle.text2sql.web.utility

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class WithLogLevelTest {

    @Test
    fun `should set and restore log level`() {
        val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        val originalLevel = rootLogger.level
        val newLevel = Level.DEBUG

        withLogLevel(newLevel) { expectThat(rootLogger.level).isEqualTo(newLevel) }

        expectThat(rootLogger.level).isEqualTo(originalLevel)
    }
}
