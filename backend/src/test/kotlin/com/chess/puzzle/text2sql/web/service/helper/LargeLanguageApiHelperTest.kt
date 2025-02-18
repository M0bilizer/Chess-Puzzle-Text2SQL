package com.chess.puzzle.text2sql.web.service.helper

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.model.ModelId
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError.*
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModel
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
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
            ChatCompletion(
                id = "1",
                created = 1L,
                model = ModelId("deepseek-chat"),
                choices =
                    listOf(
                        ChatChoice(
                            index = 0,
                            message =
                                ChatMessage(
                                    role = ChatRole.System,
                                    messageContent = TextContent(sqlQuery),
                                ),
                        )
                    ),
            )

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns chatCompletion

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

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws RateLimitException(1, OpenAIError())

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

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws
            InvalidRequestException(1, OpenAIError())

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

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws
            AuthenticationException(1, OpenAIError())

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

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws PermissionException(1, OpenAIError())

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(PermissionError)
        }
    }

    @Test
    fun `test callModel with UnknownAPIException 402`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws UnknownAPIException(402, OpenAIError())

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(InsufficientBalanceError)
        }
    }

    @Test
    fun `test callModel with UnknownAPIException 503`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws UnknownAPIException(503, OpenAIError())

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(ServerOverload)
        }
    }

    @Test
    fun `test callModel with UnknownAPIException unknown`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws UnknownAPIException(1, OpenAIError())

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(UnknownError(1, "no message"))
        }
    }

    @Test
    fun `test callModel with OpenAIHttpException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws OpenAIHttpException()

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(HttpError)
        }
    }

    @Test
    fun `test callModel with OpenAIServerException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws OpenAIServerException()

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(ServerError)
        }
    }

    @Test
    fun `test callModel with OpenAIIOException`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } throws OpenAITimeoutException(Throwable())

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(IOException)
        }
    }

    @Test
    fun `test callModel with unexpected result`(): Unit = runBlocking {
        // Arrange
        val query = "My query string"
        val promptTemplate = "Convert this query to SQL: $query"
        val chatCompletion =
            ChatCompletion(
                id = "1",
                created = 1L,
                model = ModelId("deepseek-chat"),
                choices =
                    listOf(
                        ChatChoice(
                            index = 0,
                            message =
                                ChatMessage(
                                    role = ChatRole.System,
                                    messageContent = mockk(), // Mocking an unexpected content type
                                ),
                        )
                    ),
            )

        coEvery { llmFactory.getModel(ModelName.Deepseek) } returns openAI
        coEvery { openAI.callModel(promptTemplate) } returns chatCompletion

        // Act
        val result = helper.callModel(promptTemplate, ModelName.Deepseek)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallLargeLanguageModelError>>().and {
            get { error }.isEqualTo(UnexpectedResult)
        }
    }
}
