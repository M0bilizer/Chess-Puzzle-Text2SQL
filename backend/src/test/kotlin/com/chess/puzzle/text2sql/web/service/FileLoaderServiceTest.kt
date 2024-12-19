package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.entities.helper.GetTextFileError
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileLoaderServiceTest {

    private val fileLoaderService = FileLoaderService()
    private val objectMapper = jacksonObjectMapper()

    private lateinit var tempJsonFile: File
    private lateinit var tempTextFile: File

    @BeforeEach
    fun setUp() {
        val benchmarkEntriesMap =
            listOf(
                mapOf("text" to "Find puzzles with rating > 1500"),
                mapOf("text" to "Find puzzles with rating > 2000"),
            )
        tempJsonFile = createTemporaryJsonFile(benchmarkEntriesMap)

        val textContent = "This is a test file content."
        tempTextFile = createTemporaryTextFile(textContent)
    }

    @AfterEach
    fun tearDown() {
        tempJsonFile.delete()
        tempTextFile.delete()
    }

    @Test
    fun `test getBenchmarkEntries success`() {
        // Arrange
        val benchmarkEntries =
            listOf(
                BenchmarkEntry(text = "Find puzzles with rating > 1500"),
                BenchmarkEntry(text = "Find puzzles with rating > 2000"),
            )

        // Act
        val result = fileLoaderService.getBenchmarkEntries(tempJsonFile.path)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Success(benchmarkEntries)) }
    }

    @Test
    fun `test getBenchmarkEntries failure with IOException`() {
        // Arrange
        val filePath = tempJsonFile.path
        tempJsonFile.delete()

        // Act
        val result = fileLoaderService.getBenchmarkEntries(filePath)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetBenchmarkEntriesError>>()
        val except = result as ResultWrapper.Failure
        expectThat(except.error).isA<GetBenchmarkEntriesError.IOException>()
    }

    @Test
    fun `test getTextFile success`() {
        // Arrange
        val expectedContent = "This is a test file content."

        // Act
        val result = fileLoaderService.getTextFile(tempTextFile.path)

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Success(expectedContent)) }
    }

    @Test
    fun `test getTextFile failure with IOException`() {
        // Arrange
        val filePath = tempTextFile.path
        tempTextFile.delete()

        // Act
        val result = fileLoaderService.getTextFile(filePath)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetTextFileError>>()
        val except = result as ResultWrapper.Failure
        expectThat(except.error).isA<GetTextFileError.IOException>()
    }

    private fun createTemporaryJsonFile(content: List<Map<String, Any>>): File {
        val tempFile = File.createTempFile("test", ".json")
        tempFile.writeText(objectMapper.writeValueAsString(content))
        tempFile.deleteOnExit()
        return tempFile
    }

    private fun createTemporaryTextFile(content: String): File {
        val tempFile = File.createTempFile("test", ".txt")
        tempFile.writeText(content)
        tempFile.deleteOnExit()
        return tempFile
    }
}
