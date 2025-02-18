package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.WriteToFileError
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.io.File

class JsonWriterServiceTest {
    private val jsonWriterService = JsonWriterService()

    @Test
    fun `writeToFile should return Success if writing is successful`() {
        val filePath = "test-output.json"
        val jsonString =
            """
            {
                "name": "John",
                "age": 30
            }
        """
                .trimIndent()

        val result = jsonWriterService.writeToFile(filePath, jsonString)

        expectThat(result).isA<ResultWrapper.Success<Unit>>()

        val expectedJson = jsonWriterService.objectMapper.readTree(jsonString).toPrettyString()
        val fileContent = File(filePath).readText()
        expectThat(fileContent).isEqualTo(expectedJson)

        File(filePath).delete()
    }

    @Test
    fun `writeToFile should return Failure with Exception if IOException occurs`() {
        val filePath = "invalid/path/to/file.json"
        val jsonString =
            """
            {
                "name": "John",
                "age": 30
            }
        """
                .trimIndent()

        val result = jsonWriterService.writeToFile(filePath, jsonString)

        expectThat(result).isA<ResultWrapper.Failure<WriteToFileError.Exception>>().and {
            get { error }.isA<WriteToFileError.Exception>()
        }
    }
}
