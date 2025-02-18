package com.chess.puzzle.text2sql.web.service.helper

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.exception.OpenAIIOException
import com.aallam.openai.api.exception.OpenAIServerException
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.exception.RateLimitException
import com.aallam.openai.api.exception.UnknownAPIException
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
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
 * @property largeLanguageModelFactory The [LargeLanguageModelFactory] used to get correct LLM
 *   Model.
 */
@Service
class LargeLanguageApiHelper(
    @Autowired private val largeLanguageModelFactory: LargeLanguageModelFactory
) {

    suspend fun callModel(
        query: String,
        modelName: ModelName,
    ): ResultWrapper<String, CallLargeLanguageModelError> {
        return try {
            val chatCompletion = largeLanguageModelFactory.getModel(modelName).callModel(query)
            processChatCompletionResponse(query, chatCompletion)
        } catch (e: OpenAIException) {
            handleOpenAIException(e)
        }
    }

    /**
     * Processes the [ChatCompletion] response and extracts the SQL query.
     *
     * @param query The original query sent to the API.
     * @param chatCompletion The [ChatCompletion] response from the API.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    fun processChatCompletionResponse(
        query: String,
        chatCompletion: ChatCompletion,
    ): ResultWrapper<String, CallLargeLanguageModelError> {
        return when (val response = chatCompletion.choices.firstOrNull()?.message?.messageContent) {
            is TextContent -> {
                val sql = stripUnnecessary(response.content)
                ResultWrapper.Success(sql)
            }
            else -> {
                ResultWrapper.Failure(CallLargeLanguageModelError.UnexpectedResult)
            }
        }
    }

    /**
     * Handles exceptions thrown by the OpenAI API and returns the appropriate
     * [CallLargeLanguageModelError].
     *
     * @param e The [OpenAIException] to handle.
     * @return A [ResultWrapper.Failure] containing the corresponding [CallLargeLanguageModelError].
     */
    fun handleOpenAIException(
        e: OpenAIException
    ): ResultWrapper.Failure<CallLargeLanguageModelError> {
        return when (e) {
            is OpenAIAPIException ->
                when (e) {
                    is RateLimitException ->
                        ResultWrapper.Failure(CallLargeLanguageModelError.RateLimitError)
                    is InvalidRequestException ->
                        ResultWrapper.Failure(CallLargeLanguageModelError.InvalidRequestError)
                    is AuthenticationException ->
                        ResultWrapper.Failure(CallLargeLanguageModelError.AuthenticationError)
                    is PermissionException ->
                        ResultWrapper.Failure(CallLargeLanguageModelError.PermissionError)
                    is UnknownAPIException ->
                        when (e.statusCode) {
                            402 ->
                                ResultWrapper.Failure(
                                    CallLargeLanguageModelError.InsufficientBalanceError
                                )
                            503 -> ResultWrapper.Failure(CallLargeLanguageModelError.ServerOverload)
                            else ->
                                ResultWrapper.Failure(
                                    CallLargeLanguageModelError.UnknownError(
                                        e.statusCode,
                                        e.message ?: "no message",
                                    )
                                )
                        }
                }
            is OpenAIHttpException -> ResultWrapper.Failure(CallLargeLanguageModelError.HttpError)
            is OpenAIServerException ->
                ResultWrapper.Failure(CallLargeLanguageModelError.ServerError)
            is OpenAIIOException -> ResultWrapper.Failure(CallLargeLanguageModelError.IOException)
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
    fun stripUnnecessary(string: String): String {
        return string
            .substringAfter("```")
            .substringBefore("```")
            .replace("\n", "")
            .substringAfter("sql")
            .replace(":", "")
            .substringBefore(";")
            .substringBefore("\r")
            .replace("\"", "")
    }
}
