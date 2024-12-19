package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PreprocessingHelperTest {

    private val preprocessingHelper = PreprocessingHelper()

    @Test
    fun `test processPrompt success with demonstrations`() {
        // Arrange
        val userPrompt = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "Find puzzles with rating > 2000",
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                ),
                Demonstration(
                    text = "Find puzzles with rating > 2500",
                    sql = "SELECT * FROM puzzles WHERE rating > 2500",
                ),
            )

        // Act
        val result = preprocessingHelper.processPrompt(userPrompt, promptTemplate, demonstrations)

        // Assert
        val expectedProcessedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000 Find puzzles with rating > 2500 SELECT * FROM puzzles WHERE rating > 2500"
        expectThat(result) { isEqualTo(ResultWrapper.Success(expectedProcessedPrompt)) }
    }

    @Test
    fun `test processPrompt success without demonstrations`() {
        // Arrange
        val userPrompt = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}}"
        val demonstrations: List<Demonstration>? = null

        // Act
        val result = preprocessingHelper.processPrompt(userPrompt, promptTemplate, demonstrations)

        // Assert
        val expectedProcessedPrompt = "Find puzzles with rating > 1500"
        expectThat(result) { isEqualTo(ResultWrapper.Success(expectedProcessedPrompt)) }
    }

    @Test
    fun `test processPrompt failure with IOException error`() {
        // Arrange
        val userPrompt = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}}"
        val demonstrations: List<Demonstration>? = null
        val error = ProcessPromptError.IOException(IOException())
        val mockPreprocessingHelper = mockk<PreprocessingHelper>()

        every { mockPreprocessingHelper.processPrompt(any(), any(), any()) } returns
            ResultWrapper.Failure(error)

        // Act
        val result =
            mockPreprocessingHelper.processPrompt(userPrompt, promptTemplate, demonstrations)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<ProcessPromptError>>()
        val expect = result as ResultWrapper.Failure
        expectThat(expect.error).isA<ProcessPromptError.IOException>()
    }
}
