package com.chess.puzzle.text2sql.web.utility

import io.github.oshai.kotlinlogging.KotlinLogging

class CustomLogger(private val indentLevel: Int = 0) {
    private val prefix = if (indentLevel == 0) "" else " ".repeat(indentLevel)

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val RESET = "\u001B[0m"
        private const val RED = "\u001B[31m"
        private const val BLUE = "\u001B[34m"
    }

    fun init(messageProvider: () -> String) {
        logger.info { "${prefix}${messageProvider()}" }
    }

    fun success(messageProvider: () -> String) {
        logger.info { "${prefix}${BLUE}L___>${RESET} ${messageProvider()}" }
    }

    fun error(messageProvider: () -> String) {
        logger.warn { "${prefix}${RED}L___>${RESET} ${messageProvider()}" }
    }
}
