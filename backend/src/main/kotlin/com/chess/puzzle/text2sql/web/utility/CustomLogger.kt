package com.chess.puzzle.text2sql.web.utility

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface CustomLoggingUtility {
    fun info(message: String)
}

class CustomLogger(private val logger: CustomLoggingUtility) {
    private val messages = mutableListOf<Pair<String, Int>>()
    private val mutex = Mutex()
    private var indentLevel = 0

    companion object {
        val instance: CustomLogger by lazy { CustomLogger(KotlinLoggerAdapter()) }
        const val RESET = "\u001B[0m"
        const val RED = "\u001B[31m"
        const val BLUE = "\u001B[34m"
    }

    fun info(messageProvider: () -> String) {
        synchronized(messages) { messages.add(messageProvider() to indentLevel) }
    }

    fun success(messageProvider: () -> String) {
        synchronized(messages) {
            messages.add("${BLUE}L___>$RESET ${messageProvider()}" to indentLevel)
        }
        if (indentLevel == 0) {
            printMessages(BLUE)
        }
    }

    fun error(errorMessage: () -> String) {
        synchronized(messages) {
            messages.add("${RED}L___>$RESET ${errorMessage()}" to indentLevel)
            printMessages(RED)
        }
    }

    fun error(parameters: String, errorMessage: () -> String) {
        synchronized(messages) {
            info { parameters }
            messages.add("${RED}L___>$RESET ${errorMessage()}" to indentLevel)
            printMessages(RED)
        }
    }

    suspend fun <T> withIndent(indent: Int, block: suspend () -> T): T =
        mutex.withLock {
            val previousIndent = indentLevel
            indentLevel += indent
            val result = block()
            indentLevel = previousIndent
            result
        }

    private fun printMessages(color: String) {
        synchronized(messages) {
            messages.forEachIndexed { index, (message, indent) ->
                val prefix =
                    if (index == messages.size - 1) {
                        ("${color}L_$RESET").repeat(indent)
                    } else {
                        ("${color}| $RESET").repeat(indent)
                    }
                logger.info("$prefix$message")
            }
            messages.clear()
        }
    }
}

class KotlinLoggerAdapter : CustomLoggingUtility {
    private val logger = KotlinLogging.logger {}

    override fun info(message: String) {
        logger.info { message }
    }
}
