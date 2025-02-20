package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.llm.ChatCompletionResponse
import com.chess.puzzle.text2sql.web.domain.model.llm.Choice
import com.chess.puzzle.text2sql.web.domain.model.llm.Message
import com.chess.puzzle.text2sql.web.domain.model.llm.Usage
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError.*
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModel
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class LargeLanguageApiHelperTest {

    private val llmFactory: LargeLanguageModelFactory = mockk()
    private val openAI: LargeLanguageModel = mockk()
    private lateinit var helper: LargeLanguageApiHelper

    @BeforeEach
    fun setUp() {
        helper = LargeLanguageApiHelper(llmFactory)
    }

    @Test
    fun `test callModel with successful response`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val sqlQuery = "SELECT * FROM users"
        val promptTemplate = "Convert this query to SQL: $query"
        val chatCompletion =
            ChatCompletionResponse(
                id = "1",
                `object` = "chat.completion",
                created = 1L,
                model = "deepseek-chat",
                choices =
                    listOf(
                        Choice(
                            index = 0,
                            message = Message(role = "system", content = sqlQuery),
                            finishReason = "stop",
                        )
                    ),
                usage = Usage(promptTokens = 10, completionTokens = 10, totalTokens = 20),
            )

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<ChatCompletionResponse>() } returns chatCompletion
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Success<String>>().and {
            get { data }.isEqualTo(sqlQuery)
        }
    }

    @Test
    fun `test callModel with RateLimitException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.TooManyRequests
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(RateLimitError)
        }
    }

    @Test
    fun `test callModel with InvalidRequestException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.BadRequest
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(InvalidRequestError)
        }
    }

    @Test
    fun `test callModel with AuthenticationException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.Unauthorized
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(AuthenticationError)
        }
    }

    @Test
    fun `test callModel with PermissionException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.Forbidden
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(PermissionError)
        }
    }

    @Test
    fun `test callModel with InsufficientBalanceError`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.PaymentRequired
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(InsufficientBalanceError)
        }
    }

    @Test
    fun `test callModel with ServerOverload`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.ServiceUnavailable
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(ServerOverload)
        }
    }

    @Test
    fun `test callModel with UnknownStatusError unknown`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.UpgradeRequired
        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns httpResponse

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(UnknownStatusError(426))
        }
    }

    @Test
    fun `test callModel with IOException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws IOException()

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(IOException)
        }
    }
}
