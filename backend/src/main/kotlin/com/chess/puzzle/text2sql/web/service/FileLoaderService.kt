package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.entities.helper.GetTextFileError
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream
import java.nio.charset.StandardCharsets
import org.springframework.stereotype.Service

@Service
class FileLoaderService(
    private val classLoader: ClassLoader = FileLoaderService::class.java.classLoader
) {
    private val objectMapper = jacksonObjectMapper()

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
