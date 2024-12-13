package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.FastApiResponse
import com.chess.puzzle.text2sql.web.entities.Property
import com.chess.puzzle.text2sql.web.entities.QueryRequest
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.google.gson.Gson
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * A helper service for interacting with the sentence transformer microservice.
 *
 * This class is responsible for:
 * - Making HTTP requests to the sentence transformer microservice.
 * - Finding similar demonstrations based on user input.
 * - Handling the API response and returning a [ResultWrapper] object.
 */
@Service
class SentenceTransformerHelper(@Autowired private val property: Property) {
    private val url = property.sentenceTransformerUrl
    private val partialUrl = property.sentenceTransformerPartialUrl
    private val client = HttpClient(OkHttp) { install(ContentNegotiation) { json() } }

    /**
     * Finds similar demonstrations based on the user's input using schema masking.
     *
     * This method sends the input to the sentence transformer microservice and retrieves a list of
     * similar demonstrations. The response includes a masked query and the top 3 similar
     * demonstrations.
     *
     * @param input The user's input string to find similar demonstrations for.
     * @return A [ResultWrapper] containing the list of similar demonstrations or an error.
     */
    suspend fun getSimilarDemonstration(input: String): ResultWrapper<out SimilarDemonstration> {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse =
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonString)
            }
        return if (response.status == HttpStatusCode.OK) {
            val (_, maskedQuery, demos) = response.body<FastApiResponse>()
            logger.info {
                """
                gettingSimilarDemonstration { input = $input } -> 
                    { 
                        maskedQuery = $maskedQuery, 
                        demo = [
                            ${demos[0].logTruncate()},
                            ${demos[1].logTruncate()},
                            ${demos[2].logTruncate()}]"
                    }
                """
                    .trimIndent()
            }
            ResultWrapper.Success(demos)
        } else {
            logger.warn { "gettingSimilarDemonstration { input = $input } -> ERROR" }
            ResultWrapper.Error.ResponseError
        }
    }

    /**
     * Truncates the demonstration text for logging purposes.
     *
     * This helper function ensures that the demonstration text does not clutter the logs. If the
     * text is longer than 10 characters, it is truncated and appended with "...".
     *
     * @return The truncated demonstration text.
     */
    private fun Demonstration.logTruncate(): String {
        return if (this.text.length < 10) {
            this.text
        } else {
            this.text.substring(0, 10) + "..."
        }
    }

    /**
     * Finds similar demonstrations based on the user's input without schema masking.
     *
     * This method is used for benchmarking purposes and sends the input to the sentence transformer
     * microservice without schema masking.
     *
     * @param input The user's input string to find similar demonstrations for.
     * @return A [ResultWrapper] containing the list of similar demonstrations or an error.
     */
    suspend fun getPartialSimilarDemonstration(
        input: String
    ): ResultWrapper<out SimilarDemonstration> {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse =
            client.post(partialUrl) {
                contentType(ContentType.Application.Json)
                setBody(jsonString)
            }
        return if (response.status == HttpStatusCode.OK) {
            val (_, _, demos) = response.body<FastApiResponse>()
            logger.info {
                """
                gettingPartialSimilarDemonstration { input = $input } -> 
                    { 
                        demo = [
                            ${demos[0].logTruncate()},
                            ${demos[1].logTruncate()},
                            ${demos[2].logTruncate()}]"
                    }
                    """
            }
            ResultWrapper.Success(demos)
        } else {
            logger.warn { "gettingSimilarDemonstration { input = $input } -> ERROR" }
            ResultWrapper.Error.ResponseError
        }
    }
}

/**
 * A type alias for a list of [Demonstration] objects.
 *
 * This is used to simplify the return type of methods that return similar demonstrations.
 */
typealias SimilarDemonstration = List<Demonstration>
