package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.WriteToFileError
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.IOException
import java.lang.Exception
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service responsible for writing JSON strings to files.
 *
 * This service provides a method to write a JSON string to a specified file path. It uses the
 * [ObjectMapper] from the Jackson library to parse and format the JSON string before writing it to
 * the file. The JSON output is formatted with indentation for better readability.
 */
@Service
class JsonWriterService() {
    /**
     * The [ObjectMapper] instance used to parse and format JSON strings.
     *
     * This mapper is configured to enable indentation for better readability of the output.
     */
    val objectMapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    /**
     * Writes a JSON string to a specified file path.
     *
     * This method parses the provided JSON string using the [ObjectMapper], then writes the
     * formatted JSON to the specified file. If the file does not exist, it will be created. If the
     * file already exists, it will be overwritten.
     *
     * @param filePath The path to the file where the JSON string will be written.
     * @param jsonString The JSON string to write to the file.
     * @return A [ResultWrapper] indicating success or failure. If successful, it contains `Unit`.
     *   If failed, it contains a [WriteToFileError] describing the error.
     */
    fun writeToFile(filePath: String, jsonString: String): ResultWrapper<Unit, WriteToFileError> {
        val jsonNode = objectMapper.readTree(jsonString)
        val file = File(filePath)
        try {
            objectMapper.writeValue(file, jsonNode)
        } catch (e: IOException) {
            return ResultWrapper.Failure(WriteToFileError.Exception(e))
        }
        return ResultWrapper.Success(Unit)
    }
}
