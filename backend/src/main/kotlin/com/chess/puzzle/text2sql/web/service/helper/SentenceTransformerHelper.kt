package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.config.SentenceTransformerEndpoints
import com.chess.puzzle.text2sql.web.domain.input.GenericRequest
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
        logger.info { "getting Similar Demonstration { input=${input.take(30)} }" }

        val url = sentenceTransformerEndpoints.sentenceTransformerUrl
        return fetchSimilarDemonstrations(input, url)
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
        return fetchSimilarDemonstrations(input, partialUrl)
    }

    /**
     * Fetches similar demonstrations from the Sentence Transformer microservice.
     *
     * This method sends a POST request to the specified URL with the input query and processes the
     * response.
     *
     * @param input The input query for which to fetch similar demonstrations.
     * @param url The URL of the Sentence Transformer microservice endpoint.
     * @return A [ResultWrapper] containing the list of similar demonstrations or an error.
     */
    private suspend fun fetchSimilarDemonstrations(
        input: String,
        url: String,
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val jsonString = Gson().toJson(GenericRequest(input))
        val response: HttpResponse
        try {
            response =
                client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(jsonString)
                }
        } catch (e: Exception) {
            logger.error {
                "ERROR: SentenceTransformerHelper.getSimilarDemonstration(input=$input) -> Network Error: ${e.message}"
            }
            return ResultWrapper.Failure(NetworkError)
        }

        if (response.status != HttpStatusCode.OK) {
            logger.error {
                "ERROR: SentenceTransformerHelper.getSimilarDemonstration(input=$input) -> Network Error: response.status != HttpStatusCode.OK"
            }
            return ResultWrapper.Failure(NetworkError)
        }

        val fastApiResponse: FastApiResponse
        try {
            fastApiResponse = response.body()
        } catch (e: Exception) {
            logger.error {
                "ERROR: SentenceTransformerHelper.getSimilarDemonstration(input=$input) -> Internal Error: ${e.message}"
            }
            return ResultWrapper.Failure(InternalError)
        }

        return when (fastApiResponse.status) {
            "success" -> {
                val maskedQuery = fastApiResponse.maskedQuery
                val demos = fastApiResponse.data
                logger.info {
                    "OK: SentenceTransformerHelper.getSimilarDemonstration(input=$input) -> (data=$demos, metadata=$maskedQuery)"
                }
                ResultWrapper.Success(data = demos, metadata = maskedQuery)
            }
            "failure" -> {
                logger.error {
                    "ERROR: SentenceTransformerHelper.getSimilarDemonstration(input=$input) -> Network Error: fastapiResponse.status == 'failure'"
                }
                ResultWrapper.Failure(InternalError)
            }
            else -> {
                logger.error {
                    "ERROR: SentenceTransformerHelper.getSimilarDemonstration(input=$input) -> Network Error: Unknown fastapiResponse.status"
                }
                ResultWrapper.Failure(InternalError)
            }
        }
    }
}
