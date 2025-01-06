package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.config.SentenceTransformerEndpoints
import com.chess.puzzle.text2sql.web.domain.input.QueryRequest
import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError.InternalError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError.NetworkError
import com.chess.puzzle.text2sql.web.integration.FastApiResponse
import com.google.gson.Gson
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service class for interacting with the Sentence Transformer microservice.
 *
 * This class handles:
 * - Fetching similar demonstrations for a given input query.
 * - Fetching partial similar demonstrations for a given input query.
 * - Logging and error handling for the microservice interactions.
 *
 * @property sentenceTransformerEndpoints The configuration for Sentence Transformer endpoints.
 * @property client The HTTP client used to make requests to the microservice.
 */
@Service
class SentenceTransformerHelper(
    @Autowired private val sentenceTransformerEndpoints: SentenceTransformerEndpoints,
    @Autowired private val client: HttpClient,
) {

    /**
     * Fetches similar demonstrations for the given input query.
     *
     * @param input The input query for which to fetch similar demonstrations.
     * @return A [ResultWrapper] containing the list of similar demonstrations or an error.
     */
    suspend fun getSimilarDemonstration(
        input: String
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val url = sentenceTransformerEndpoints.sentenceTransformerUrl
        return fetchSimilarDemonstrations(input, url, "gettingSimilarDemonstration")
    }

    /**
     * Fetches partial similar demonstrations for the given input query.
     *
     * @param input The input query for which to fetch partial similar demonstrations.
     * @return A [ResultWrapper] containing the list of partial similar demonstrations or an error.
     */
    suspend fun getPartialSimilarDemonstration(
        input: String
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val partialUrl = sentenceTransformerEndpoints.partialSentenceTransformerUrl
        return fetchSimilarDemonstrations(input, partialUrl, "gettingPartialSimilarDemonstration")
    }

    /**
     * Fetches similar demonstrations from the Sentence Transformer microservice.
     *
     * This method sends a POST request to the specified URL with the input query and processes the
     * response.
     *
     * @param input The input query for which to fetch similar demonstrations.
     * @param url The URL of the Sentence Transformer microservice endpoint.
     * @param logPrefix A prefix for logging messages.
     * @return A [ResultWrapper] containing the list of similar demonstrations or an error.
     */
    private suspend fun fetchSimilarDemonstrations(
        input: String,
        url: String,
        logPrefix: String,
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse
        try {
            response =
                client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(jsonString)
                }
        } catch (e: Exception) {
            logger.error { "$logPrefix { input = $input } -> Network Error: ${e.message}" }
            return ResultWrapper.Failure(NetworkError)
        }

        if (response.status != HttpStatusCode.OK) {
            logger.warn { "$logPrefix { input = $input } -> Network Error" }
            return ResultWrapper.Failure(NetworkError)
        }

        val fastApiResponse: FastApiResponse
        try {
            fastApiResponse = response.body()
        } catch (e: Exception) {
            return ResultWrapper.Failure(InternalError)
        }

        return when (fastApiResponse.status) {
            "success" -> {
                val maskedQuery = fastApiResponse.maskedQuery
                val demos = fastApiResponse.data
                logSuccess(logPrefix, input, maskedQuery, demos)
                ResultWrapper.Success(demos)
            }
            "failure" -> {
                logger.warn { "$logPrefix { input = $input } -> Network Error" }
                ResultWrapper.Failure(InternalError)
            }
            else -> {
                logger.warn { "$logPrefix { input = $input} -> Internal Error" }
                ResultWrapper.Failure(InternalError)
            }
        }
    }

    /**
     * Logs the successful retrieval of similar demonstrations.
     *
     * @param logPrefix A prefix for logging messages.
     * @param input The input query for which similar demonstrations were fetched.
     * @param maskedQuery The masked query returned by the microservice.
     * @param demos The list of similar demonstrations.
     * @param maxTruncateLength The maximum length to truncate demonstration text for logging.
     */
    private fun logSuccess(
        logPrefix: String,
        input: String,
        maskedQuery: String,
        demos: List<Demonstration>,
        maxTruncateLength: Int = 10,
    ) {
        fun Demonstration.truncate(): String {
            return if (this.text.length < maxTruncateLength) {
                this.text
            } else {
                this.text.substring(0, maxTruncateLength) + "..."
            }
        }

        logger.info {
            """$logPrefix { input = $input } -> { maskedQuery = $maskedQuery, demo = [${demos[0].truncate()},${demos[1].truncate()},${demos[2].truncate()}]"}"""
                .trimIndent()
        }
    }
}
