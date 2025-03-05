package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.llm.ChatCompletionResponse
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
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
        logger.info { "Calling Model { query = ${query.take(10)}, modelName = $modelName }" }
        val parameters = Parameters(query.take(10), modelName)

        val response: HttpResponse
        try {
            response = largeLanguageModelFactory.getModel(modelName).callModel(query)
        } catch (e: Exception) {
            return handleHttpException(parameters, e)
        }
        if (!response.status.isSuccess()) {
            return handleUnsuccessfulResponse(parameters, response)
        }
        val chatCompletion = response.body<ChatCompletionResponse>()
        val textResponse = chatCompletion.choices.first().message.content
        val sql = stripUnnecessary(textResponse)
        logger.info {
            "OK: LargeLanguageApiHelper.callModel(query=${query.take(10)}, modelName=$modelName) -> (sql=$sql)"
        }
        return ResultWrapper.Success(sql)
    }

    private fun handleUnsuccessfulResponse(
        parameters: Parameters,
        response: HttpResponse,
    ): ResultWrapper.Failure<CallLargeLanguageModelError> {
        val (query, modelName) = parameters
        return when (response.status) {
            HttpStatusCode.TooManyRequests -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.TooManyRequests <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.RateLimitError)
            }
            HttpStatusCode.BadRequest -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.BadRequest <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.InvalidRequestError)
            }
            HttpStatusCode.Unauthorized -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.Unauthorized <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.AuthenticationError)
            }
            HttpStatusCode.Forbidden -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.Forbidden <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.PermissionError)
            }
            HttpStatusCode.PaymentRequired -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.PaymentRequired <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.InsufficientBalanceError)
            }
            HttpStatusCode.ServiceUnavailable -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.ServiceUnavailable <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.ServerOverload)
            }
            else -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.UnknownStatusError <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
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
    private fun handleHttpException(
        parameters: Parameters,
        e: Exception,
    ): ResultWrapper.Failure<CallLargeLanguageModelError> {
        val (query, modelName) = parameters
        return when (e) {
            is HttpRequestTimeoutException -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.TimeoutError <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.TimeoutError)
            }
            is ServerResponseException -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.ServerError <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.ServerError)
            }
            is IOException -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.IOException <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
                ResultWrapper.Failure(CallLargeLanguageModelError.IOException)
            }
            else -> {
                logger.error {
                    "ERROR: CallLargeLanguageModelError.UnknownError <- LargeLanguageApiHelper.callModel(query=$query, modelName=$modelName)"
                }
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
    private fun stripUnnecessary(string: String): String {
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

private data class Parameters(val query: String, val modelName: ModelName)
