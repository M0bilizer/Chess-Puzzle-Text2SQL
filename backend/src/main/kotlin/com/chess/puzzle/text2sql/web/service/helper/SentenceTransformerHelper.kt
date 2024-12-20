package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.config.SentenceTransformerEndpoints
import com.chess.puzzle.text2sql.web.entities.*
import com.chess.puzzle.text2sql.web.entities.helper.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.entities.helper.GetSimilarDemonstrationError.InternalError
import com.chess.puzzle.text2sql.web.entities.helper.GetSimilarDemonstrationError.NetworkError
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

@Service
class SentenceTransformerHelper(
    @Autowired private val sentenceTransformerEndpoints: SentenceTransformerEndpoints,
    @Autowired private val client: HttpClient,
) {

    suspend fun getSimilarDemonstration(
        input: String
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val url = sentenceTransformerEndpoints.sentenceTransformerUrl
        return fetchSimilarDemonstrations(input, url, "gettingSimilarDemonstration")
    }

    suspend fun getPartialSimilarDemonstration(
        input: String
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val partialUrl = sentenceTransformerEndpoints.partialSentenceTransformerUrl
        return fetchSimilarDemonstrations(input, partialUrl, "gettingPartialSimilarDemonstration")
    }

    private suspend fun fetchSimilarDemonstrations(
        input: String,
        url: String,
        logPrefix: String,
    ): ResultWrapper<List<Demonstration>, GetSimilarDemonstrationError> {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse =
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonString)
            }

        if (response.status != HttpStatusCode.OK) return ResultWrapper.Failure(NetworkError)

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
                ResultWrapper.Failure(NetworkError)
            }
            else -> {
                logger.warn { "$logPrefix { input = $input} -> Internal Error" }
                ResultWrapper.Failure(InternalError)
            }
        }
    }

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
