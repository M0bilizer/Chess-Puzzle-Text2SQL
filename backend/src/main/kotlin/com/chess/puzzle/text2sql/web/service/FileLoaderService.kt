package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.BenchmarkEntry
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetBenchmarkEntriesError
import com.chess.puzzle.text2sql.web.entities.helper.GetTextFileError
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import org.springframework.stereotype.Service

@Service
class FileLoaderService {
    private val objectMapper = jacksonObjectMapper()

    fun getBenchmarkEntries(
        filePath: String
    ): ResultWrapper<List<BenchmarkEntry>, GetBenchmarkEntriesError> {
        return try {
            val jsonContent = File(filePath).readText().replace(Regex("/\\*(.|\\R)*?\\*/"), "")
            val benchmarkEntries = objectMapper.readValue<List<BenchmarkEntry>>(jsonContent)
            ResultWrapper.Success(benchmarkEntries)
        } catch (e: java.io.IOException) {
            ResultWrapper.Failure(GetBenchmarkEntriesError.IOException(e))
        }
    }

    fun getTextFile(filePath: String): ResultWrapper<String, GetTextFileError> {
        return try {
            val string = File(filePath).readText()
            ResultWrapper.Success(string)
        } catch (e: java.io.IOException) {
            ResultWrapper.Failure(GetTextFileError.IOException(e))
        }
    }
}
