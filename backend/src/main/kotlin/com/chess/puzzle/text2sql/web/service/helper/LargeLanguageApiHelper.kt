package com.chess.puzzle.text2sql.web.service.helper

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.Content
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.chess.puzzle.text2sql.web.entities.Property
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * A helper service for interacting with the DeepSeek large language model API.
 *
 * This class is responsible for:
 * - Making HTTP requests to the DeepSeek API.
 * - Converting user queries into SQL statements using the API.
 * - Handling the API response and returning a [ResultWrapper] object.
 */
@Service
class LargeLanguageApiHelper(@Autowired private val property: Property) {
    private val apiKey = property.apiKey
    private val baseUrl = property.baseUrl
    private val loggingConfig =
        LoggingConfig(logLevel = LogLevel.None, logger = Logger.Simple, sanitize = true)
    private val client: OpenAI =
        OpenAI(token = apiKey, host = OpenAIHost(baseUrl), logging = loggingConfig)

    /**
     * Sends a query to the DeepSeek API and returns the result as a [ResultWrapper].
     *
     * This method is a shorthand for [callDeepSeek] with no prompt template.
     *
     * @param query The user's query to be converted into SQL.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    suspend fun callDeepSeek(query: String): ResultWrapper<out String> {
        val loggingString = if (query.length > 20) query.substring(0, 20) else query
        return this.callDeepSeek(loggingString, query)
    }

    /**
     * Sends a query and a custom prompt template to the DeepSeek API and returns the result as a
     * [ResultWrapper].
     *
     * @param query The user's query to be converted into SQL.
     * @param promptTemplate A custom prompt template to be sent to the API.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    suspend fun callDeepSeek(query: String, promptTemplate: String): ResultWrapper<out String> {
        val chatCompletionRequest =
            ChatCompletionRequest(
                model = ModelId("deepseek-chat"),
                messages =
                    listOf(
                        ChatMessage(
                            role = ChatRole.System,
                            content = "You are a helpful assistant",
                        ),
                        ChatMessage(role = ChatRole.User, content = promptTemplate),
                    ),
                temperature = 0.0,
            )
        val chatCompletion = client.chatCompletion(chatCompletionRequest)
        return when (
            val response: Content? = chatCompletion.choices.firstOrNull()?.message?.messageContent
        ) {
            is TextContent -> {
                logger.info {
                    "Calling DeepSeek { query = $query } -> { response = ${response.content} }"
                }
                val sql = stripUnnecessary(response.content)
                ResultWrapper.Success(sql)
            }
            else -> {
                logger.warn {
                    "Calling DeepSeek { query = $query } -> { Received image and texts }"
                }
                ResultWrapper.Error.ResponseError
            }
        }
    }

    /**
     * Strips unnecessary characters and formatting from the API response to extract the SQL query.
     *
     * DeepSeek might add unnecessary characters (e.g., Markdown formatting) to the response, which
     * this method removes.
     *
     * @param string The raw response string from the API.
     * @return The cleaned SQL query.
     */
    private fun stripUnnecessary(string: String): String {
        return string
            .substringAfter("```")
            .substringBefore("```")
            .substringAfter("\n")
            .substringBefore("\n")
            .substringAfter("sql: ")
            .substringBefore(";")
            .substringBefore("\r")
            .replace("\"", "")
    }
}
