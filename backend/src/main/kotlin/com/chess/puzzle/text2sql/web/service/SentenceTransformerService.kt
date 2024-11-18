package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.helper.Demonstration
import com.chess.puzzle.text2sql.web.helper.PropertyHelper
import com.chess.puzzle.text2sql.web.helper.QueryRequest
import com.chess.puzzle.text2sql.web.helper.ResultWrapper
import com.google.gson.Gson
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SentenceTransformerService (
    @Autowired
    private val propertyHelper: PropertyHelper
) {
    private val url = propertyHelper.sentenceTransformerUrl
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }


    suspend fun getSimilarDemonstration(input: String): ResultWrapper {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(jsonString)
        }
        return if (response.status == HttpStatusCode.OK) {
            val data = response.body<List<Demonstration>>()
            logger.info {"gettingSimilarDemonstration { input = $input } -> [${data[0]}, ${data[1]}, ${data[2]}"}
            ResultWrapper.DemonstrationDataSuccess(data)
        } else {
            logger.warn {"gettingSimilarDemonstration { input = $input } -> ERROR"}
            ResultWrapper.ResponseError
        }
    }
}