package com.chess.puzzle.text2sql.web.service.helper

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.CallDeepSeekError
import com.chess.puzzle.text2sql.web.error.CallDeepSeekError.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class LargeLanguageApiHelperTest {

    private val mockOpenAI: OpenAI = mockk()
    private val helper = LargeLanguageApiHelper(mockOpenAI)

    @Test
    fun `test callDeepSeek with successful response`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val query = "SELECT * FROM users"
        val promptTemplate = "Convert this query to SQL: $input"
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
                                    messageContent = TextContent(query),
                                ),
                        )
                    ),
            )

        coEvery { mockOpenAI.chatCompletion(any()) } returns chatCompletion

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Success<String>>().and {
            get { data }.isEqualTo("SELECT * FROM users")
        }
    }

    @Test
    fun `test callDeepSeek with RateLimitException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws RateLimitException(1, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(RateLimitError)
        }
    }

    @Test
    fun `test callDeepSeek with InvalidRequestException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws
            InvalidRequestException(1, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(InvalidRequestError)
        }
    }

    @Test
    fun `test callDeepSeek with AuthenticationException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws
            AuthenticationException(1, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(AuthenticationError)
        }
    }

    @Test
    fun `test callDeepSeek with PermissionException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws PermissionException(1, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(PermissionError)
        }
    }

    @Test
    fun `test callDeepSeek with UnknownAPIException 402`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws UnknownAPIException(402, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(InsufficientBalanceError)
        }
    }

    @Test
    fun `test callDeepSeek with UnknownAPIException 503`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws UnknownAPIException(503, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(ServerOverload)
        }
    }

    @Test
    fun `test callDeepSeek with UnknownAPIException unknown`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws UnknownAPIException(1, OpenAIError())

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(UnknownError(1, "no message"))
        }
    }

    @Test
    fun `test callDeepSeek with OpenAIHttpException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws OpenAIHttpException()

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(HttpError)
        }
    }

    @Test
    fun `test callDeepSeek with OpenAIServerException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        coEvery { mockOpenAI.chatCompletion(any()) } throws OpenAIServerException()

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(ServerError)
        }
    }

    @Test
    fun `test callDeepSeek with OpenAIIOException`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        val mockException: OpenAIIOException = mockk()
        coEvery { mockOpenAI.chatCompletion(any()) } throws mockException

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(IOException)
        }
    }

    @Test
    fun `test callDeepSeek with unexpected result`(): Unit = runBlocking {
        // Arrange
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"
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

        coEvery { mockOpenAI.chatCompletion(any()) } returns chatCompletion

        // Act
        val result = helper.callDeepSeek(promptTemplate)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<CallDeepSeekError>>().and {
            get { error }.isEqualTo(UnexpectedResult)
        }
    }
}
