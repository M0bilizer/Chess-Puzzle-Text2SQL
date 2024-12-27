package com.chess.puzzle.text2sql.web.service.helper

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.CallDeepSeekError
import com.chess.puzzle.text2sql.web.entities.helper.CallDeepSeekError.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service class for interacting with the DeepSeek API.
 *
 * This class handles sending queries to the DeepSeek API, processing the responses, and converting
 * them into SQL queries. It also handles exceptions and errors returned by the API.
 *
 * @property client The [OpenAI] client used to interact with the DeepSeek API.
 */
@Service
class LargeLanguageApiHelper(@Autowired private val client: OpenAI) {

    /**
     * Sends a query to the DeepSeek API and returns the result as a [ResultWrapper].
     *
     * @param query The user's query to be converted into SQL.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    suspend fun callDeepSeek(query: String): ResultWrapper<String, CallDeepSeekError> {
        val chatCompletionRequest = createChatCompletionRequest(query)
        return try {
            val chatCompletion = client.chatCompletion(chatCompletionRequest)
            processChatCompletionResponse(query, chatCompletion)
        } catch (e: OpenAIException) {
            handleOpenAIException(e)
        }
    }

    /**
     * Creates a [ChatCompletionRequest] for the given prompt template.
     *
     * @param input The custom prompt template to be sent to the API.
     * @return A [ChatCompletionRequest] object.
     */
    private fun createChatCompletionRequest(input: String): ChatCompletionRequest {
        return ChatCompletionRequest(
            model = ModelId("deepseek-chat"),
            messages =
                listOf(
                    ChatMessage(role = ChatRole.System, content = "You are a helpful assistant"),
                    ChatMessage(role = ChatRole.User, content = input),
                ),
            temperature = 0.0,
        )
    }

    /**
     * Processes the [ChatCompletion] response and extracts the SQL query.
     *
     * @param query The original query sent to the API.
     * @param chatCompletion The [ChatCompletion] response from the API.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    private fun processChatCompletionResponse(
        query: String,
        chatCompletion: ChatCompletion,
    ): ResultWrapper<String, CallDeepSeekError> {
        return when (val response = chatCompletion.choices.firstOrNull()?.message?.messageContent) {
            is TextContent -> {
                val sql = stripUnnecessary(response.content)
                ResultWrapper.Success(sql)
            }
            else -> {
                ResultWrapper.Failure(UnexpectedResult)
            }
        }
    }

    /**
     * Handles exceptions thrown by the OpenAI API and returns the appropriate [CallDeepSeekError].
     *
     * @param e The [OpenAIException] to handle.
     * @return A [ResultWrapper.Failure] containing the corresponding [CallDeepSeekError].
     */
    private fun handleOpenAIException(
        e: OpenAIException
    ): ResultWrapper.Failure<CallDeepSeekError> {
        return when (e) {
            is OpenAIAPIException ->
                when (e) {
                    is RateLimitException -> ResultWrapper.Failure(RateLimitError)
                    is InvalidRequestException -> ResultWrapper.Failure(InvalidRequestError)
                    is AuthenticationException -> ResultWrapper.Failure(AuthenticationError)
                    is PermissionException -> ResultWrapper.Failure(PermissionError)
                    is UnknownAPIException ->
                        when (e.statusCode) {
                            402 -> ResultWrapper.Failure(InsufficientBalanceError)
                            503 -> ResultWrapper.Failure(ServerOverload)
                            else ->
                                ResultWrapper.Failure(
                                    UnknownError(e.statusCode, e.message ?: "no message")
                                )
                        }
                }
            is OpenAIHttpException -> ResultWrapper.Failure(HttpError)
            is OpenAIServerException -> ResultWrapper.Failure(ServerError)
            is OpenAIIOException -> ResultWrapper.Failure(IOException)
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
            .replace("\n", "")
            .substringAfter("sql: ")
            .substringBefore(";")
            .substringBefore("\r")
            .replace("\"", "")
    }
}
