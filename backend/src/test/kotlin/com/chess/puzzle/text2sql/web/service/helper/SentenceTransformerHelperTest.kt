package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.config.SentenceTransformerEndpoints
import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.FastApiResponse
import com.chess.puzzle.text2sql.web.entities.QueryRequest
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.entities.helper.GetSimilarDemonstrationError.*
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.every
import io.mockk.mockk
import kotlin.text.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class SentenceTransformerHelperTest {

    @Test
    fun `test getSimilarDemonstration with successful response`(): Unit = runBlocking {
        // Arrange
        val input = "example query"
        val expectedUrl = "http://example.com/sentence-transformer"
        val expectedResponse =
            FastApiResponse(
                status = "success",
                maskedQuery = "masked query",
                data =
                    listOf(
                        Demonstration("text0", "sql0"),
                        Demonstration("text1", "sql1"),
                        Demonstration("text2", "sql2"),
                    ),
            )

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }

        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content = Json.encodeToString(expectedResponse),
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }

        // Create the helper with mocked dependencies
        val helper = SentenceTransformerHelper(mockEndpoints, client)

        // Act
        val result = helper.getSimilarDemonstration(input)

        // Assert
        expectThat(result).isA<ResultWrapper.Success<List<Demonstration>>>().and {
            get { data }.isEqualTo(expectedResponse.data)
        }

        val expectedRequestBody = Gson().toJson(QueryRequest(input))
        val request =
            mockEngine.requestHistory.firstOrNull {
                it.url.toString() == expectedUrl && it.method == HttpMethod.Post
            }
        expectThat(request).isNotNull().and {
            get { body.contentType }.isEqualTo(ContentType.Application.Json)
            runBlocking {
                val bodyText = request!!.body.toByteReadPacket().readText()
                get { bodyText }.isEqualTo(expectedRequestBody)
            }
        }
    }

    @Test
    fun `test getSimilarDemonstration with network error`(): Unit = runBlocking {
        // Arrange
        val input = "example query"
        val expectedUrl = "http://example.com/sentence-transformer"

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }

        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respondError(HttpStatusCode.NotFound) // Simulate a network error
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }

        // Create the helper with mocked dependencies
        val helper = SentenceTransformerHelper(mockEndpoints, client)

        // Act
        val result = helper.getSimilarDemonstration(input)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetSimilarDemonstrationError>>().and {
            get { error }.isEqualTo(NetworkError)
        }

        val request =
            mockEngine.requestHistory.firstOrNull {
                it.url.toString() == expectedUrl && it.method == HttpMethod.Post
            }
        expectThat(request).isNotNull().and {
            get { body.contentType }.isEqualTo(ContentType.Application.Json)
            runBlocking {
                val bodyText = request!!.body.toByteReadPacket().readText()
                get { bodyText }.isEqualTo(Gson().toJson(QueryRequest(input)))
            }
        }
    }

    @Test
    fun `test getSimilarDemonstration with internal error`(): Unit = runBlocking {
        // Arrange
        val input = "example query"
        val expectedUrl = "http://example.com/sentence-transformer"

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }

        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content =
                        Json.encodeToString(
                            FastApiResponse(
                                status = "failure", // Simulate a failure status
                                maskedQuery = "masked query",
                                data = emptyList(),
                            )
                        ),
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }

        val helper = SentenceTransformerHelper(mockEndpoints, client)

        // Act
        val result = helper.getSimilarDemonstration(input)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetSimilarDemonstrationError>>().and {
            get { error }.isEqualTo(NetworkError)
        }

        val request =
            mockEngine.requestHistory.firstOrNull {
                it.url.toString() == expectedUrl && it.method == HttpMethod.Post
            }
        expectThat(request).isNotNull().and {
            get { body.contentType }.isEqualTo(ContentType.Application.Json)
            runBlocking {
                val bodyText = request!!.body.toByteReadPacket().readText()
                get { bodyText }.isEqualTo(Gson().toJson(QueryRequest(input)))
            }
        }
    }

    @Test
    fun `test getSimilarDemonstration with invalid response structure`(): Unit = runBlocking {
        // Arrange
        val input = "example query"
        val expectedUrl = "http://example.com/sentence-transformer"

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }

        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content = """{"invalidField": "invalidValue"}""",
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val client =
            HttpClient(mockEngine) {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }

        val helper = SentenceTransformerHelper(mockEndpoints, client)

        // Act
        val result = helper.getSimilarDemonstration(input)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetSimilarDemonstrationError>>().and {
            get { error }.isEqualTo(InternalError)
        }

        val request =
            mockEngine.requestHistory.firstOrNull {
                it.url.toString() == expectedUrl && it.method == HttpMethod.Post
            }
        expectThat(request).isNotNull().and {
            get { body.contentType }.isEqualTo(ContentType.Application.Json)
            runBlocking {
                val bodyText = request!!.body.toByteReadPacket().readText()
                get { bodyText }.isEqualTo(Gson().toJson(QueryRequest(input)))
            }
        }
    }

    @Test
    fun `test getSimilarDemonstration with empty response`(): Unit = runBlocking {
        // Arrange
        val input = "example query"
        val expectedUrl = "http://example.com/sentence-transformer"

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }

        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }

        val helper = SentenceTransformerHelper(mockEndpoints, client)

        // Act
        val result = helper.getSimilarDemonstration(input)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetSimilarDemonstrationError>>().and {
            get { error }.isEqualTo(InternalError)
        }

        val request =
            mockEngine.requestHistory.firstOrNull {
                it.url.toString() == expectedUrl && it.method == HttpMethod.Post
            }
        expectThat(request).isNotNull().and {
            get { body.contentType }.isEqualTo(ContentType.Application.Json)
            runBlocking {
                val bodyText = request!!.body.toByteReadPacket().readText()
                get { bodyText }.isEqualTo(Gson().toJson(QueryRequest(input)))
            }
        }
    }
}
