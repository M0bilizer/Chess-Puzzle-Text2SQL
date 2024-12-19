package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ModelName
import com.chess.puzzle.text2sql.web.entities.ModelName.*
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.CustomError
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class Text2SQLService(
    @Autowired private val filePaths: FilePaths,
    @Autowired private val fileLoaderService: FileLoaderService,
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val preprocessingHelper: PreprocessingHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
) {
    suspend fun convertToSQL(
        query: String,
        modelName: ModelName,
    ): ResultWrapper<String, CustomError> {
        return when (modelName) {
            Full -> full(query)
            Partial -> partial(query)
            Baseline -> baseline(query)
        }
    }

    private suspend fun full(query: String): ResultWrapper<String, CustomError> {
        val promptTemplate: String
        val demonstrations: List<Demonstration>
        val processedPrompt: String
        val sql: String
        when (val result = fileLoaderService.getTextFile(filePaths.getPromptTemplate(Full))) {
            is ResultWrapper.Success -> promptTemplate = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = sentenceTransformerHelper.getSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (
            val result = preprocessingHelper.processPrompt(query, promptTemplate, demonstrations)
        ) {
            is ResultWrapper.Success -> processedPrompt = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = largeLanguageApiHelper.callDeepSeek(query, processedPrompt)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }

    private suspend fun partial(query: String): ResultWrapper<String, CustomError> {
        val promptTemplate: String
        val demonstrations: List<Demonstration>
        val processedPrompt: String
        val sql: String
        when (val result = fileLoaderService.getTextFile(filePaths.getPromptTemplate(Partial))) {
            is ResultWrapper.Success -> promptTemplate = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = sentenceTransformerHelper.getPartialSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (
            val result = preprocessingHelper.processPrompt(query, promptTemplate, demonstrations)
        ) {
            is ResultWrapper.Success -> processedPrompt = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = largeLanguageApiHelper.callDeepSeek(query, processedPrompt)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }

    private suspend fun baseline(query: String): ResultWrapper<String, CustomError> {
        val promptTemplate: String
        val processedPrompt: String
        val sql: String
        when (val result = fileLoaderService.getTextFile(filePaths.getPromptTemplate(Full))) {
            is ResultWrapper.Success -> promptTemplate = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = preprocessingHelper.processPrompt(query, promptTemplate, null)) {
            is ResultWrapper.Success -> processedPrompt = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = largeLanguageApiHelper.callDeepSeek(query, processedPrompt)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }
}
