package com.chess.puzzle.text2sql.web.utility

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomLoggerTest {

    private lateinit var customLogger: CustomLogger
    private val mockLogger: CustomLoggingUtility = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        customLogger = CustomLogger(mockLogger)
    }

    @Test
    fun `test info logging`() {
        customLogger.info { "Test info message" }

        // Since info just adds to the list, we need to trigger a print to verify
        customLogger.success { "Trigger print" }

        verify { mockLogger.info(match { it.contains("Test info message") }) }
    }

    @Test
    fun `test success logging with indent level 0`() {
        customLogger.success { "Test success message" }

        verify { mockLogger.info(match { it.contains("Test success message") }) }
    }

    @Test
    fun `test success logging with indent level greater than 0`() {
        runBlocking {
            customLogger.withIndent(1) { customLogger.success { "Indented success message" } }
            customLogger.success { "Trigger print" }
        }

        verify { mockLogger.info(match { it.contains("Indented success message") }) }
    }

    @Test
    fun `test error logging`() {
        customLogger.error { "Test error message" }

        verify { mockLogger.info(match { it.contains("Test error message") }) }
    }

    @Test
    fun `test error logging with parameters`() {
        customLogger.error("Parameters") { "Test error message with parameters" }

        verify { mockLogger.info(match { it.contains("Parameters") }) }
        verify { mockLogger.info(match { it.contains("Test error message with parameters") }) }
    }

    @Test
    fun `test indentation handling`() {
        runBlocking {
            customLogger.withIndent(2) { customLogger.info { "Indented message" } }
            customLogger.success { "Trigger print" }
        }

        verify { mockLogger.info(match { it.contains("Indented message") }) }
    }
}
