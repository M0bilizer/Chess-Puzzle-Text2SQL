package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.helper.Demonstration
import com.chess.puzzle.text2sql.web.entities.helper.FastApiResponse
import com.chess.puzzle.text2sql.web.entities.helper.Property
import com.chess.puzzle.text2sql.web.entities.helper.QueryRequest
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
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

@Service
class SentenceTransformerHelper(@Autowired private val property: Property) {
    private val url = property.sentenceTransformerUrl
    private val partialUrl = property.sentenceTransformerPartialUrl
    private val client = HttpClient(OkHttp) { install(ContentNegotiation) { json() } }

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

    private fun Demonstration.logTruncate(): String {
        return if (this.text.length < 10) {
            this.text
        } else {
            this.text.substring(0, 10) + "..."
        }
    }

    // Benchmarking purposes
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

typealias SimilarDemonstration = List<Demonstration>
