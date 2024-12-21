package com.chess.puzzle.text2sql.web.service.helper

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.ProcessPromptError
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
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
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
                Demonstration(
                    text = "Find puzzles with rating > 3000",
                    sql = "SELECT * FROM puzzles WHERE rating > 3000",
                ),
            )

        // Act
        val result = preprocessingHelper.processPrompt(userPrompt, promptTemplate, demonstrations)

        // Assert
        val expectedProcessedPrompt =
            "Find puzzles with rating > 1500 Find puzzles with rating > 2000 SELECT * FROM puzzles WHERE rating > 2000 Find puzzles with rating > 2500 SELECT * FROM puzzles WHERE rating > 2500 Find puzzles with rating > 3000 SELECT * FROM puzzles WHERE rating > 3000"
        expectThat(result) { isEqualTo(ResultWrapper.Success(expectedProcessedPrompt)) }
    }

    // Test: Success without demonstrations
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
    fun `test processPrompt failure with InvalidDemonstrationError`() {
        // Arrange
        val userPrompt = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
        val demonstrations =
            listOf(
                Demonstration(
                    text = "", // Invalid: empty text
                    sql = "SELECT * FROM puzzles WHERE rating > 2000",
                )
            )

        // Act
        val result = preprocessingHelper.processPrompt(userPrompt, promptTemplate, demonstrations)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<ProcessPromptError>>().and {
            get { error }.isEqualTo(ProcessPromptError.InvalidDemonstrationError)
        }
    }

    // Test: Failure due to InsufficientDemonstrationsError
    @Test
    fun `test processPrompt failure with InsufficientDemonstrationsError`() {
        // Arrange
        val userPrompt = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}} {{text1}} {{sql1}} {{text2}} {{sql2}}"
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
        expectThat(result).isA<ResultWrapper.Failure<ProcessPromptError>>().and {
            get { error }.isEqualTo(ProcessPromptError.InsufficientDemonstrationsError)
        }
    }

    // Test: Failure due to MissingPlaceholderError
    @Test
    fun `test processPrompt failure with MissingPlaceholderError`() {
        // Arrange
        val userPrompt = "Find puzzles with rating > 1500"
        val promptTemplate = "{{prompt}} {{text0}} {{sql0}}"
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
                Demonstration(
                    text = "Find puzzles with rating > 3000",
                    sql = "SELECT * FROM puzzles WHERE rating > 3000",
                ),
            )

        // Act
        val result = preprocessingHelper.processPrompt(userPrompt, promptTemplate, demonstrations)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<ProcessPromptError>>().and {
            get { error }.isEqualTo(ProcessPromptError.MissingPlaceholderError)
        }
    }
}
