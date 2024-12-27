package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.entities.helper.GetTextFileError
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class FileLoaderServiceTest {

    private val fileLoaderService = FileLoaderService()

    @Test
    fun `test getBenchmarkEntries success`() {
        // Arrange
        val benchmarkEntries =
            listOf(
                BenchmarkEntry(text = "Find puzzles with rating > 1500"),
                BenchmarkEntry(text = "Find puzzles with rating > 2000"),
            )

        // Act
        val result = fileLoaderService.getBenchmarkEntries("benchmarkEntries.json")

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Success(benchmarkEntries)) }
    }

    @Test
    fun `test getBenchmarkEntries failure with FileNotFound`() {
        // Arrange
        val filePath = "nonExistentFile.json"

        // Act
        val result = fileLoaderService.getBenchmarkEntries(filePath)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetBenchmarkEntriesError>>().and {
            get { error }.isA<GetBenchmarkEntriesError.FileNotFoundError>()
        }
    }

    @Test
    fun `test getBenchmarkEntries failure with IOException`() {
        // Arrange
        val filePath = "corruptedFile.json"
        val mockClassLoader = mockk<ClassLoader>()
        every { mockClassLoader.getResourceAsStream(filePath) } throws
            IOException("Simulated IOException")
        val fileLoaderService = FileLoaderService(mockClassLoader)

        // Act
        val result = fileLoaderService.getBenchmarkEntries(filePath)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetBenchmarkEntriesError>>().and {
            get { error }.isA<GetBenchmarkEntriesError.IOException>()
        }
    }

    @Test
    fun `test getTextFile success`() {
        // Arrange
        val expectedContent = "This is a test file content."

        // Act
        val result = fileLoaderService.getTextFile("testTextFile.txt")

        // Assert
        expectThat(result) { isEqualTo(ResultWrapper.Success(expectedContent)) }
    }

    @Test
    fun `test getTextFile failure with FileNotFound`() {
        // Arrange
        val filePath = "nonExistentFile.txt"

        // Act
        val result = fileLoaderService.getTextFile(filePath)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetTextFileError>>().and {
            get { error }.isA<GetTextFileError.FileNotFoundError>()
        }
    }

    @Test
    fun `test getTextFile failure with IOException`() {
        // Arrange
        val filePath = "corruptedFile.txt"
        val mockClassLoader = mockk<ClassLoader>()
        every { mockClassLoader.getResourceAsStream(filePath) } throws
            IOException("Simulated IOException")
        val fileLoaderService = FileLoaderService(mockClassLoader)

        // Act
        val result = fileLoaderService.getTextFile(filePath)

        // Assert
        expectThat(result).isA<ResultWrapper.Failure<GetTextFileError>>().and {
            get { error }.isA<GetTextFileError.IOException>()
        }
    }
}
