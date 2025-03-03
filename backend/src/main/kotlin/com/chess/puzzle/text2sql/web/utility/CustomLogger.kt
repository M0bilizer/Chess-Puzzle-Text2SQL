import io.github.oshai.kotlinlogging.KotlinLogging

object CustomLogger {
    private val logger = KotlinLogging.logger {}
    private val messages = mutableListOf<Pair<String, Int>>()
    private var indentLevel = 0

    fun info(messageProvider: () -> String) {
        synchronized(messages) {
            messages.add(messageProvider() to indentLevel)
        }
    }

    fun success(message: String = "OK!") {
        synchronized(messages) {
            messages.add("└---> $message" to indentLevel)
        }
    }

    fun error(messageProvider: () -> String, exception: Exception? = null) {
        synchronized(messages) {
            messages.add("ERROR: ${messageProvider()}" to indentLevel)
            exception?.let {
                messages.add("Exception: ${it.message}" to indentLevel)
                messages.add("Stack Trace: ${it.stackTraceToString()}" to indentLevel)
            }
            printLogs()
        }
    }

    fun <T> withIndent(indent: Int, block: suspend () -> T): T {
        synchronized(messages) {
            val previousIndent = indentLevel
            indentLevel += indent
            val result = block()
            indentLevel = previousIndent
            return result
        }
    }

    private fun printLogs() {
        synchronized(messages) {
            messages.forEach { (message, indent) ->
                val prefix = "| ".repeat(indent)
                logger.info { "$prefix$message" }
            }
            logger.info { "Error" }
            messages.clear()
        }
    }
}
