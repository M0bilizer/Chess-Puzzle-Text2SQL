package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.llm.ChatCompletionResponse
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
import com.chess.puzzle.text2sql.web.utility.CustomLogger
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val customLogger = CustomLogger(indentLevel = 2)

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
        customLogger.init { "LargeLanguageApiHelper.callModel(query=${query.take(10)}, modelName=$modelName)" }
        val response: HttpResponse
        try {
            response = largeLanguageModelFactory.getModel(modelName).callModel(query)
        } catch (e: Exception) {
            return handleHttpException(e)
        }
        if (!response.status.isSuccess()) {
            return handleUnsuccessfulResponse(response)
        }
        val chatCompletion = response.body<ChatCompletionResponse>()
        val textResponse = chatCompletion.choices.first().message.content
        val sql = stripUnnecessary(textResponse)
        customLogger.success { "(sql=$sql)" }
        return ResultWrapper.Success(sql)
    }

    fun handleUnsuccessfulResponse(
        response: HttpResponse
    ): ResultWrapper.Failure<CallLargeLanguageModelError> {
        return when (response.status) {
            HttpStatusCode.TooManyRequests -> {
                customLogger.error { "CallLargeLanguageModelError.TooManyRequests" }
                ResultWrapper.Failure(CallLargeLanguageModelError.RateLimitError)
            }
            HttpStatusCode.BadRequest -> {
                customLogger.error { "CallLargeLanguageModelError.BadRequest" }
                ResultWrapper.Failure(CallLargeLanguageModelError.InvalidRequestError)
            }
            HttpStatusCode.Unauthorized -> {
                customLogger.error { "CallLargeLanguageModelError.Unauthorized" }
                ResultWrapper.Failure(CallLargeLanguageModelError.AuthenticationError)
            }
            HttpStatusCode.Forbidden -> {
                customLogger.error { "CallLargeLanguageModelError.Forbidden" }
                ResultWrapper.Failure(CallLargeLanguageModelError.PermissionError)
            }
            HttpStatusCode.PaymentRequired -> {
                customLogger.error { "CallLargeLanguageModelError.PaymentRequired" }
                ResultWrapper.Failure(CallLargeLanguageModelError.InsufficientBalanceError)
            }
            HttpStatusCode.ServiceUnavailable -> {
                customLogger.error { "CallLargeLanguageModelError.ServiceUnavailable" }
                ResultWrapper.Failure(CallLargeLanguageModelError.ServerOverload)
            }
            else -> {
                customLogger.error { "CallLargeLanguageModelError.UnknownStatusError" }
                ResultWrapper.Failure(
                    CallLargeLanguageModelError.UnknownStatusError(response.status.value)
                )
            }
        }
    }

    /**
     * Handles exceptions thrown by the OpenAI API and returns the appropriate
     * [CallLargeLanguageModelError].
     *
     * @param e The [Exception] to handle.
     * @return A [ResultWrapper.Failure] containing the corresponding [CallLargeLanguageModelError].
     */
    fun handleHttpException(e: Exception): ResultWrapper.Failure<CallLargeLanguageModelError> {
        print(e)
        return when (e) {
            is HttpRequestTimeoutException -> {
                customLogger.error { "CallLargeLanguageModelError.TimeoutError" }
                ResultWrapper.Failure(CallLargeLanguageModelError.TimeoutError)
            }
            is ServerResponseException -> {
                customLogger.error { "CallLargeLanguageModelError.ServerError" }
                ResultWrapper.Failure(CallLargeLanguageModelError.ServerError)
            }
            is IOException -> {
                customLogger.error { "CallLargeLanguageModelError.IOException" }
                ResultWrapper.Failure(CallLargeLanguageModelError.IOException)
            }
            else -> {
                customLogger.error { "CallLargeLanguageModelError.UnknownError" }
                ResultWrapper.Failure(
                    CallLargeLanguageModelError.UnknownError(-1, e.message ?: "no message")
                )
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
