package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.WriteToFileError
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

class JsonWriterServiceTest {

    private val jsonWriterService = JsonWriterService()
    private val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

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
