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
 * This class handles sending search queries to the DeepSeek API, processing the responses,
 * converting them into SQL queries, and handling exceptions. It uses the
 * [LargeLanguageModelFactory] to get the appropriate LLM model based on the [ModelName].
 *
 * @property largeLanguageModelFactory The factory used to create the appropriate LLM model.
 */
@Service
class LargeLanguageApiHelper(
    @Autowired private val largeLanguageModelFactory: LargeLanguageModelFactory
) {

    /**
     * Calls the specified model with the given query and returns the result as an SQL query.
     *
     * This function handles the entire flow of sending the query to the model, processing the
     * response, extracting the SQL query, and handling any errors.
     *
     * @param query The search query string to be converted into SQL.
     * @param modelName The [ModelName] specifying which LLM model to use (e.g., Deepseek or
     *   Mistral).
     * @return A [ResultWrapper] containing either the SQL query or an error.
     */
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

    /**
     * Handles HTTP errors from the API response and returns the corresponding error.
     *
     * This function maps HTTP status codes to specific [CallLargeLanguageModelError] values, logs
     * the error, and returns a [ResultWrapper.Failure].
     *
     * @param parameters The [Parameters] holding the query and model name for logging purposes.
     * @param response The [HttpResponse] containing the error status.
     * @return A [ResultWrapper.Failure] with the corresponding error.
     */
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
     * Handles exceptions thrown during the API call and returns the corresponding error.
     *
     * This function catches and processes exceptions, mapping them to specific
     * [CallLargeLanguageModelError] values, logging the error, and returning a
     * [ResultWrapper.Failure].
     *
     * @param parameters The [Parameters] holding the query and model name for logging purposes.
     * @param e The [Exception] to handle.
     * @return A [ResultWrapper.Failure] with the corresponding error.
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
     * Processes the API response to extract and clean up the SQL query.
     *
     * This function strips unnecessary characters, formatting, and noise from the raw API response
     * to ensure the extracted SQL query is clean and usable.
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

/**
 * A private helper class storing temporary parameters used for logging.
 *
 * @property query The query string being processed.
 * @property modelName The model name being used.
 */
private data class Parameters(val query: String, val modelName: ModelName)
