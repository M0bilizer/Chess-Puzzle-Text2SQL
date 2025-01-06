package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.CallDeepSeekError
import com.chess.puzzle.text2sql.web.error.GetSimilarDemonstrationError
import com.chess.puzzle.text2sql.web.error.GetTextFileError
import com.chess.puzzle.text2sql.web.error.ProcessPromptError
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Text2SQLServiceTest {

    private val filePaths: FilePaths = mockk()
    private val fileLoaderService: FileLoaderService = mockk()
    private val sentenceTransformerHelper: SentenceTransformerHelper = mockk()
    private val preprocessingHelper: PreprocessingHelper = mockk()
    private val largeLanguageApiHelper: LargeLanguageApiHelper = mockk()

    private val text2SQLService =
        Text2SQLService(
            filePaths,
            fileLoaderService,
            sentenceTransformerHelper,
            preprocessingHelper,
            largeLanguageApiHelper,
        )

    @Test
    fun `test convertToSQL success for Full model`(): Unit = runBlocking {
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

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Success(sql)

        val result = text2SQLService.convertToSQL(query, ModelName.Full)

        expectThat(result) { isEqualTo(ResultWrapper.Success(sql)) }
    }

    @Test
    fun `test convertToSQL failure in file loading`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val error = GetTextFileError.IOException(IOException())

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Full)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in sentence transformer`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val error = GetSimilarDemonstrationError.InternalError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Full)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in preprocessing`(): Unit = runBlocking {
        // Arrange
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

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Full)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in large language API`(): Unit = runBlocking {
        // Arrange
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
        val error = CallDeepSeekError.InsufficientBalanceError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns "full_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("full_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Full)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL success for Partial model`(): Unit = runBlocking {
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

        coEvery { filePaths.getPromptTemplate(ModelName.Partial) } returns
            "partial_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("partial_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getPartialSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Success(sql)

        val result = text2SQLService.convertToSQL(query, ModelName.Partial)

        expectThat(result) { isEqualTo(ResultWrapper.Success(sql)) }
    }

    @Test
    fun `test convertToSQL failure in file loading for Partial model`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val error = GetTextFileError.IOException(IOException())

        coEvery { filePaths.getPromptTemplate(ModelName.Partial) } returns
            "partial_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("partial_prompt_template.txt") } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Partial)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in sentence transformer for Partial model`(): Unit =
        runBlocking {
            // Arrange
            val query = "Find puzzles with rating > 1500"
            val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
            val error = GetSimilarDemonstrationError.NetworkError

            coEvery { filePaths.getPromptTemplate(ModelName.Partial) } returns
                "partial_prompt_template.txt"
            coEvery { fileLoaderService.getTextFile("partial_prompt_template.txt") } returns
                ResultWrapper.Success(promptTemplate)
            coEvery { sentenceTransformerHelper.getPartialSimilarDemonstration(query) } returns
                ResultWrapper.Failure(error)

            // Act
            val result = text2SQLService.convertToSQL(query, ModelName.Partial)

            // Assert
            expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
        }

    @Test
    fun `test convertToSQL failure in preprocessing for Partial model`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )
        val error = ProcessPromptError.InvalidDemonstrationError

        coEvery { filePaths.getPromptTemplate(ModelName.Partial) } returns
            "partial_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("partial_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getPartialSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Partial)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in large language API for Partial model`(): Unit = runBlocking {
        // Arrange
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
        val error = CallDeepSeekError.RateLimitError

        coEvery { filePaths.getPromptTemplate(ModelName.Partial) } returns
            "partial_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("partial_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { sentenceTransformerHelper.getPartialSimilarDemonstration(query) } returns
            ResultWrapper.Success(demonstrations)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, demonstrations) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Partial)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL success for Baseline model`(): Unit = runBlocking {
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}}"
        val processedPrompt = "Find puzzles with rating > 1500"
        val sql = "SELECT * FROM puzzles WHERE rating > 1500"

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns
            "baseline_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("baseline_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, null) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Success(sql)

        val result = text2SQLService.convertToSQL(query, ModelName.Baseline)

        expectThat(result) { isEqualTo(ResultWrapper.Success(sql)) }
    }

    @Test
    fun `test convertToSQL failure in file loading for Baseline model`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val error = GetTextFileError.FileNotFoundError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns
            "baseline_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("baseline_prompt_template.txt") } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Baseline)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in preprocessing for Baseline model`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}}"
        val error = ProcessPromptError.MissingPlaceholderError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns
            "baseline_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("baseline_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, null) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Baseline)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }

    @Test
    fun `test convertToSQL failure in large language API for Baseline model`(): Unit = runBlocking {
        // Arrange
        val query = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}}"
        val processedPrompt = "Find puzzles with rating > 1500"
        val error = CallDeepSeekError.PermissionError

        coEvery { filePaths.getPromptTemplate(ModelName.Full) } returns
            "baseline_prompt_template.txt"
        coEvery { fileLoaderService.getTextFile("baseline_prompt_template.txt") } returns
            ResultWrapper.Success(promptTemplate)
        coEvery { preprocessingHelper.processPrompt(query, promptTemplate, null) } returns
            ResultWrapper.Success(processedPrompt)
        coEvery { largeLanguageApiHelper.callDeepSeek(processedPrompt) } returns
            ResultWrapper.Failure(error)

        // Act
        val result = text2SQLService.convertToSQL(query, ModelName.Baseline)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Failure(error)) }
    }
}
