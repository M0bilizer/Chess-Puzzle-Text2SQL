package com.chess.puzzle.text2sql.web.controllers

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.config.SentenceTransformerEndpoints
import com.chess.puzzle.text2sql.web.domain.input.GenericRequest
import com.chess.puzzle.text2sql.web.domain.input.LlmRequest
import com.chess.puzzle.text2sql.web.domain.input.PromptTemplateRequest
import com.chess.puzzle.text2sql.web.domain.input.Text2SqlRequest
import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.domain.model.llm.ChatCompletionResponse
import com.chess.puzzle.text2sql.web.domain.model.llm.Choice
import com.chess.puzzle.text2sql.web.domain.model.llm.Message
import com.chess.puzzle.text2sql.web.domain.model.llm.Usage
import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.error.CallLargeLanguageModelError
import com.chess.puzzle.text2sql.web.error.GetRandomPuzzlesError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.GetTextFileError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError
import com.chess.puzzle.text2sql.web.error.ProcessQueryError
import com.chess.puzzle.text2sql.web.integration.FastApiResponse
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.service.FileLoaderService
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.service.Text2SQLService
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModel
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
import com.chess.puzzle.text2sql.web.utility.ResponseUtils
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.IOException

class DebuggingControllerIntegrationTest {
    private val puzzleRepository: PuzzleRepository = mockk()
    private val sqlValidator: SqlValidator = mockk()
    private val puzzleService = PuzzleService(puzzleRepository, sqlValidator)

    private val preprocessingHelper = PreprocessingHelper()

    private val filePaths: FilePaths = mockk()
    private val fileLoaderService: FileLoaderService = mockk()
    private val sentenceTransformerHelperMock: SentenceTransformerHelper = mockk()
    private val preprocessingHelperMock: PreprocessingHelper = mockk()
    private val largeLanguageApiHelperMock: LargeLanguageApiHelper = mockk()
    private val text2SQLService =
        Text2SQLService(
            filePaths,
            fileLoaderService,
            sentenceTransformerHelperMock,
            preprocessingHelperMock,
            largeLanguageApiHelperMock,
        )

    private lateinit var sentenceTransformerHelper: SentenceTransformerHelper

    private val largeLanguageModel: LargeLanguageModel = mockk()
    private val largeLanguageModelFactory: LargeLanguageModelFactory = mockk()
    private val largeLanguageApiHelper = LargeLanguageApiHelper(largeLanguageModelFactory)

    @Test
    fun `test hello endpoint`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )
        val response = controller.hello()
        expectThat(response).isEqualTo("Hello from Spring Boot!")
    }

    @Test
    fun `test db endpoint success`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val puzzles =
            listOf(
                Puzzle(
                    id = 0,
                    puzzleId = "00sHx",
                    fen = "r3k2r/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                    moves = "e8d7 a2e6 d7d8 f7f8",
                    rating = 1760,
                    ratingDeviation = 80,
                    popularity = 83,
                    nbPlays = 72,
                    themes = "mate mateIn2 middlegame short",
                    gameUrl = "https://lichess.org/yyznGmXs/black#34",
                    openingTags = "Italian_Game Italian_Game_Classical_Variation",
                )
            )

        coEvery { puzzleRepository.findRandomPuzzles(5) } returns puzzles

        val response = controller.db()
        val expectedResponse = ResponseUtils.success(puzzles)

        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test db endpoint failure`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val error = GetRandomPuzzlesError.Throwable(Throwable())
        coEvery { puzzleRepository.findRandomPuzzles(5) } throws Throwable()

        val response = controller.db()
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sql endpoint success`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val query = "SELECT * FROM puzzles"
        val genericRequest = GenericRequest(query)
        val puzzles =
            listOf(
                Puzzle(
                    id = 0,
                    puzzleId = "00sHx",
                    fen = "r3k2r/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                    moves = "e8d7 a2e6 d7d8 f7f8",
                    rating = 1760,
                    ratingDeviation = 80,
                    popularity = 83,
                    nbPlays = 72,
                    themes = "mate mateIn2 middlegame short",
                    gameUrl = "https://lichess.org/yyznGmXs/black#34",
                    openingTags = "Italian_Game Italian_Game_Classical_Variation",
                )
            )

        coEvery { sqlValidator.isValidSql(query) } returns true
        coEvery { sqlValidator.isAllowed(query) } returns true
        coEvery { puzzleRepository.executeSqlQuery(query) } returns puzzles

        val response = controller.sql(genericRequest)
        val expectedResponse = ResponseUtils.success(puzzles)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sql endpoint failure in executeSqlQuery`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val query = "SELECT * FROM puzzles"
        val genericRequest = GenericRequest(query)
        val error = ProcessQueryError.HibernateError

        coEvery { sqlValidator.isValidSql(query) } returns true
        coEvery { sqlValidator.isAllowed(query) } returns true
        coEvery { puzzleRepository.executeSqlQuery(query) } throws Throwable()

        val response = controller.sql(genericRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sql endpoint failure in isAllowed`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val query = "SELECT * FROM puzzles"
        val genericRequest = GenericRequest(query)
        val error = ProcessQueryError.ValidationError(isValid = true, isAllowed = false)

        coEvery { sqlValidator.isValidSql(query) } returns true
        coEvery { sqlValidator.isAllowed(query) } returns false

        val response = controller.sql(genericRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sql endpoint failure in isValid`() {
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val query = "SELECT * FROM puzzles"
        val genericRequest = GenericRequest(query)
        val error = ProcessQueryError.ValidationError(isValid = false, isAllowed = true)

        coEvery { sqlValidator.isValidSql(query) } returns false
        coEvery { sqlValidator.isAllowed(query) } returns true

        val response = controller.sql(genericRequest)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sentenceTransformer endpoint success`(): Unit = runBlocking {
        val expectedUrl = "http://example.com/sentence-transformer"
        val similarDemonstrations =
            listOf(
                Demonstration("text0", "sql0"),
                Demonstration("text1", "sql1"),
                Demonstration("text2", "sql2"),
            )
        val fastapiResponse =
            FastApiResponse(
                status = "success",
                maskedQuery = "masked query",
                data = similarDemonstrations,
            )

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }
        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                respond(
                    content = Json.encodeToString(fastapiResponse),
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }
        sentenceTransformerHelper = SentenceTransformerHelper(mockEndpoints, client)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelper,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val genericRequest = GenericRequest(query = "some query")
        val response = controller.sentenceTransformer(genericRequest)

        val expectedResponse = ResponseUtils.success(similarDemonstrations)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test promptTemplate endpoint success`(): Unit = runBlocking {
        val query = "some prompt"
        val variant = ModelVariant.Full
        val promptTemplateRequest = PromptTemplateRequest(query, variant.toString())
        val filepath = "some/file/path"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
        val demonstrations =
            listOf(
                Demonstration("myText0", "mySql0"),
                Demonstration("myText1", "mySql1"),
                Demonstration("myText2", "mySql2"),
            )
        val processedPrompt =
            "$query ${demonstrations[0].text} ${demonstrations[0].sql} ${demonstrations[1].text} ${demonstrations[1].sql} ${demonstrations[2].text} ${demonstrations[2].sql}"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns filepath
        coEvery { fileLoaderService.getTextFile(filepath) } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)

        val response =
            DebuggingController(
                    filePaths = filePaths,
                    fileLoaderService = fileLoaderService,
                    sentenceTransformerHelper = sentenceTransformerHelperMock,
                    preprocessingHelper = preprocessingHelper,
                    largeLanguageApiHelper = largeLanguageApiHelperMock,
                    text2SQLService = text2SQLService,
                    puzzleService = puzzleService,
                )
                .promptTemplate(promptTemplateRequest)

        val expectedResponse = ResponseUtils.success(processedPrompt)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test promptTemplate endpoint failure in fileloading`(): Unit = runBlocking {
        val query = "some prompt"
        val variant = ModelVariant.Full
        val promptTemplateRequest = PromptTemplateRequest(query, variant.toString())
        val filepath = "some/file/path"
        val error = GetTextFileError.FileNotFoundError
        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns filepath
        coEvery { fileLoaderService.getTextFile(filepath) } returns ResultWrapper.Failure(error)

        val response =
            DebuggingController(
                    filePaths = filePaths,
                    fileLoaderService = fileLoaderService,
                    sentenceTransformerHelper = sentenceTransformerHelperMock,
                    preprocessingHelper = preprocessingHelper,
                    largeLanguageApiHelper = largeLanguageApiHelperMock,
                    text2SQLService = text2SQLService,
                    puzzleService = puzzleService,
                )
                .promptTemplate(promptTemplateRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test promptTemplate endpoint failure in getting similar demonstrations`(): Unit =
        runBlocking {
            val query = "some prompt"
            val variant = ModelVariant.Full
            val promptTemplateRequest = PromptTemplateRequest(query, variant.toString())
            val filepath = "some/file/path"
            val promptTemplate =
                "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
            val error = GetSimilarDemonstrationError.NetworkError

            coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns filepath
            coEvery { fileLoaderService.getTextFile(filepath) } returns
                ResultWrapper.Success(promptTemplate)
            coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
                ResultWrapper.Failure(error)

            val response =
                DebuggingController(
                        filePaths = filePaths,
                        fileLoaderService = fileLoaderService,
                        sentenceTransformerHelper = sentenceTransformerHelperMock,
                        preprocessingHelper = preprocessingHelper,
                        largeLanguageApiHelper = largeLanguageApiHelperMock,
                        text2SQLService = text2SQLService,
                        puzzleService = puzzleService,
                    )
                    .promptTemplate(promptTemplateRequest)

            val expectedResponse = ResponseUtils.failure(error)

            expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
            expectThat(response.body).isEqualTo(expectedResponse.body)
        }

    @Test
    fun `test promptTemplate endpoint failure in processing prompt`(): Unit = runBlocking {
        val query = "some prompt"
        val variant = ModelVariant.Full
        val promptTemplateRequest = PromptTemplateRequest(query, variant.toString())
        val filepath = "some/file/path"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
        val demonstrations = listOf<Demonstration>()
        val error = ProcessPromptError.InsufficientDemonstrationsError

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns filepath
        coEvery { fileLoaderService.getTextFile(filepath) } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)

        val response =
            DebuggingController(
                    filePaths = filePaths,
                    fileLoaderService = fileLoaderService,
                    sentenceTransformerHelper = sentenceTransformerHelperMock,
                    preprocessingHelper = preprocessingHelper,
                    largeLanguageApiHelper = largeLanguageApiHelperMock,
                    text2SQLService = text2SQLService,
                    puzzleService = puzzleService,
                )
                .promptTemplate(promptTemplateRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sentenceTransformer endpoint network error`(): Unit = runBlocking {
        val expectedUrl = "http://example.com/sentence-transformer"

        val mockEndpoints =
            mockk<SentenceTransformerEndpoints> {
                every { sentenceTransformerUrl } returns expectedUrl
            }
        val mockEngine = MockEngine { request ->
            if (request.url.toString() == expectedUrl && request.method == HttpMethod.Post) {
                throw IOException("Connection error")
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json() } }
        sentenceTransformerHelper = SentenceTransformerHelper(mockEndpoints, client)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelper,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val genericRequest = GenericRequest(query = "some query")
        val response = controller.sentenceTransformer(genericRequest)

        val error = GetSimilarDemonstrationError.NetworkError
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test sentenceTransformer endpoint internal error`(): Unit = runBlocking {
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
                                status = "failure",
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
        sentenceTransformerHelper = SentenceTransformerHelper(mockEndpoints, client)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelper,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val genericRequest = GenericRequest(query = "some query")
        val response = controller.sentenceTransformer(genericRequest)

        val error = GetSimilarDemonstrationError.InternalError
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint success`(): Unit = runBlocking {
        val input = "My query string"
        val query = "SELECT * FROM users"
        val promptTemplate = "Convert this query to SQL: $input"
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
                            message = Message(role = "system", content = query),
                            finishReason = "stop",
                        )
                    ),
                usage = Usage(promptTokens = 10, completionTokens = 10, totalTokens = 20),
            )

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<ChatCompletionResponse>() } returns chatCompletion
        coEvery { largeLanguageModelFactory.getModel(ModelName.Deepseek) } returns
            largeLanguageModel
        coEvery { largeLanguageModel.callModel(promptTemplate) } returns httpResponse
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val llmRequest = LlmRequest(promptTemplate)
        val response = controller.llm(llmRequest)

        val expectedResponse = ResponseUtils.success(query)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint failure RateLimitException`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status.isSuccess() } returns false
        coEvery { httpResponse.status } returns HttpStatusCode.TooManyRequests
        coEvery { largeLanguageModelFactory.getModel(ModelName.Deepseek) } returns
            largeLanguageModel
        coEvery { largeLanguageModel.callModel(promptTemplate) } returns httpResponse
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val llmRequest = LlmRequest(promptTemplate)
        val response = controller.llm(llmRequest)

        val error = CallLargeLanguageModelError.RateLimitError
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint failure PermissionException`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status.isSuccess() } returns false
        coEvery { httpResponse.status } returns HttpStatusCode.Forbidden
        coEvery { largeLanguageModelFactory.getModel(ModelName.Deepseek) } returns
            largeLanguageModel
        coEvery { largeLanguageModel.callModel(promptTemplate) } returns httpResponse
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val llmRequest = LlmRequest(promptTemplate)
        val response = controller.llm(llmRequest)

        val error = CallLargeLanguageModelError.PermissionError
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint failure Insufficient Balance`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status.isSuccess() } returns false
        coEvery { httpResponse.status } returns HttpStatusCode.PaymentRequired
        coEvery { largeLanguageModelFactory.getModel(ModelName.Deepseek) } returns
            largeLanguageModel
        coEvery { largeLanguageModel.callModel(promptTemplate) } returns httpResponse
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val llmRequest = LlmRequest(promptTemplate)
        val response = controller.llm(llmRequest)

        val error = CallLargeLanguageModelError.InsufficientBalanceError
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test llm endpoint failure Up unknown`(): Unit = runBlocking {
        val input = "My query string"
        val promptTemplate = "Convert this query to SQL: $input"

        val httpResponse: HttpResponse = mockk()
        coEvery { httpResponse.status } returns HttpStatusCode.TooEarly
        coEvery { largeLanguageModelFactory.getModel(ModelName.Deepseek) } returns
            largeLanguageModel
        coEvery { largeLanguageModel.callModel(promptTemplate) } returns httpResponse
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelper,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val llmRequest = LlmRequest(promptTemplate)
        val response = controller.llm(llmRequest)

        val error = CallLargeLanguageModelError.UnknownStatusError(425)
        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint success`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery {
            preprocessingHelperMock.processPrompt(query, promptTemplate, demonstrations)
        } returns ResultWrapper.Success(processedPrompt)
        coEvery {
            largeLanguageApiHelperMock.callModel(processedPrompt, ModelName.Deepseek)
        } returns ResultWrapper.Success(sql)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val text2SqlRequest = Text2SqlRequest(query)
        val response = controller.text2sql(text2SqlRequest)

        val expectedResponse = ResponseUtils.success(sql)
        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint error in file loading`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val error = GetTextFileError.IOException(IOException())

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val text2SqlRequest = Text2SqlRequest(query)
        val response = controller.text2sql(text2SqlRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint failure in sentence transformer`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val error = GetSimilarDemonstrationError.InternalError

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val text2SqlRequest = Text2SqlRequest(query)
        val response = controller.text2sql(text2SqlRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint failure in preprocessing`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val error = ProcessPromptError.MissingPlaceholderError

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery {
            preprocessingHelperMock.processPrompt(query, promptTemplate, demonstrations)
        } returns ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val text2SqlRequest = Text2SqlRequest(query)
        val response = controller.text2sql(text2SqlRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }

    @Test
    fun `test text2sql endpoint failure in large language api`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val processedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000"
        val error = CallLargeLanguageModelError.InsufficientBalanceError

        coEvery { filePaths.getPromptTemplate(ModelVariant.Full) } returns
            "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelperMock.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery {
            preprocessingHelperMock.processPrompt(query, promptTemplate, demonstrations)
        } returns ResultWrapper.Success(processedPrompt)
        coEvery {
            largeLanguageApiHelperMock.callModel(processedPrompt, ModelName.Deepseek)
        } returns ResultWrapper.Failure(error)
        val controller =
            DebuggingController(
                filePaths = filePaths,
                fileLoaderService = fileLoaderService,
                sentenceTransformerHelper = sentenceTransformerHelperMock,
                preprocessingHelper = preprocessingHelperMock,
                largeLanguageApiHelper = largeLanguageApiHelperMock,
                text2SQLService = text2SQLService,
                puzzleService = puzzleService,
            )

        val text2SqlRequest = Text2SqlRequest(query)
        val response = controller.text2sql(text2SqlRequest)

        val expectedResponse = ResponseUtils.failure(error)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isEqualTo(expectedResponse.body)
    }
}
