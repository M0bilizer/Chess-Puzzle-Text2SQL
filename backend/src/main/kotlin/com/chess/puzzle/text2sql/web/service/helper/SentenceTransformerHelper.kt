package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.helper.Demonstration
import com.chess.puzzle.text2sql.web.entities.helper.Property
import com.chess.puzzle.text2sql.web.entities.helper.QueryRequest
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.google.gson.Gson
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
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
    @Autowired
    private val propertyHelper: Property,
) {
    private val url = propertyHelper.sentenceTransformerUrl
    private val client = HttpClient(OkHttp)

    suspend fun getSimilarDemonstration(input: String): ResultWrapper<out List<Demonstration>> {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse =
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonString)
            }
        return if (response.status == HttpStatusCode.OK) {
            val data = response.body<List<Demonstration>>()
            logger.info { "gettingSimilarDemonstration { input = $input } -> [${data[0]}, ${data[1]}, ${data[2]}" }
            ResultWrapper.Success(data)
        } else {
            logger.warn { "gettingSimilarDemonstration { input = $input } -> ERROR" }
            ResultWrapper.Error.ResponseError
        }
    }
}
