package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.domain.model.BenchmarkEntry
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.error.GetTextFileError
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Service class for loading files from the classpath.
 *
 * This class handles:
 * - Loading benchmark entries from a JSON file.
 * - Loading text files from the classpath.
 *
 * @property classLoader The class loader used to access resources.
 * @property objectMapper The Jackson object mapper for JSON deserialization.
 */
@Service
class FileLoaderService(
    private val classLoader: ClassLoader = FileLoaderService::class.java.classLoader
) {
    private val objectMapper = jacksonObjectMapper()

    /**
     * Loads benchmark entries from a JSON file.
     *
     * This method reads a JSON file from the classpath, removes comments, and deserializes it into
     * a list of [BenchmarkEntry] objects.
     *
     * @param filePath The path to the JSON file in the classpath.
     * @return A [ResultWrapper] containing the list of benchmark entries or an error.
     */
    fun getBenchmarkEntries(
        filePath: String
    ): ResultWrapper<List<BenchmarkEntry>, GetBenchmarkEntriesError> {
        return try {
            val inputStream: InputStream? = classLoader.getResourceAsStream(filePath)
            if (inputStream == null) {
                ResultWrapper.Failure(GetBenchmarkEntriesError.FileNotFoundError)
            } else {
                val jsonContent =
                    inputStream
                        .bufferedReader(StandardCharsets.UTF_8)
                        .use { it.readText() }
                        .replace(Regex("/\\*(.|\\R)*?\\*/"), "") // Remove comments
                val benchmarkEntries = objectMapper.readValue<List<BenchmarkEntry>>(jsonContent)
                ResultWrapper.Success(benchmarkEntries)
            }
        } catch (e: java.io.IOException) {
            ResultWrapper.Failure(GetBenchmarkEntriesError.IOException(e))
        }
    }

    /**
     * Loads a text file from the classpath.
     *
     * This method reads a text file from the classpath and returns its content as a string.
     *
     * @param filePath The path to the text file in the classpath.
     * @return A [ResultWrapper] containing the file content or an error.
     */
    fun getTextFile(filePath: String): ResultWrapper<String, GetTextFileError> {
        return try {
            val inputStream: InputStream? = classLoader.getResourceAsStream(filePath)
            if (inputStream == null) {
                ResultWrapper.Failure(GetTextFileError.FileNotFoundError)
            } else {
                val string =
                    inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
                ResultWrapper.Success(string)
            }
        } catch (e: java.io.IOException) {
            ResultWrapper.Failure(GetTextFileError.IOException(e))
        }
    }
}
