package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.helper.Demonstration
import com.chess.puzzle.text2sql.web.entities.helper.Property
import com.chess.puzzle.text2sql.web.entities.helper.QueryRequest
import com.chess.puzzle.text2sql.web.entities.helper.ResponseDto
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
class SentenceTransformerHelper(
    @Autowired
    private val propertyHelper: Property,
) {
    private val url = propertyHelper.sentenceTransformerUrl
    private val client =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json()
            }
        }

    suspend fun getSimilarDemonstration(input: String): ResultWrapper<out List<Demonstration>> {
        val jsonString = Gson().toJson(QueryRequest(input))
        val response: HttpResponse =
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonString)
            }
        return if (response.status == HttpStatusCode.OK) {
            val data = response.body<ResponseDto<List<Demonstration>>>().data
            logger.info { "gettingSimilarDemonstration { input = $input } -> [${data[0]}, ${data[1]}, ${data[2]}" }
            ResultWrapper.Success(data)
        } else {
            logger.warn { "gettingSimilarDemonstration { input = $input } -> ERROR" }
            ResultWrapper.Error.ResponseError
        }
    }
    fun flippingMatrix(matrix: Array<Array<Int>>): Int {
        var total = 0
        var size = matrix.size / 2
        for (row in 0 until size) {
            for (col in 0 until size) {
                var max: Int = Integer.MIN_VALUE
                max = if (max < matrix[row][col]) matrix[row][col] else max
                max = if (max < matrix[row][2*size - col - 1]) matrix[row][2*size - col - 1] else max
                max = if (max < matrix[2 * size - row - 1][col]) matrix[2 * size - row - 1][col] else max
                max = if (max < matrix[2 * size - row - 1][2 * size - col - 1])matrix[2 * size - row - 1][2 * size - col - 1] else max
                total += max
            }
        }
        return total
    }
}