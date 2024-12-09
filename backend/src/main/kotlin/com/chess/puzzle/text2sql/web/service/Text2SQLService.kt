package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.helper.Demonstration
import com.chess.puzzle.text2sql.web.entities.helper.ResultWrapper
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class Text2SQLService(
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val preprocessingHelper: PreprocessingHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
) {
    suspend fun convertToSQL(query: String): ResultWrapper<out String> {
        val demonstrations: List<Demonstration>
        when (val result = sentenceTransformerHelper.getSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
        val promptTemplate = preprocessingHelper.processPrompt(query, demonstrations)
        return when (val result = largeLanguageApiHelper.callDeepSeek(query, promptTemplate)) {
            is ResultWrapper.Success -> ResultWrapper.Success(result.data)
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
    }

    // Benchmarking purposes

    suspend fun partialConvertToSQL(query: String): ResultWrapper<out String> {
        val demonstrations: List<Demonstration>
        when (val result = sentenceTransformerHelper.getPartialSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
        val promptTemplate = preprocessingHelper.processPrompt(query, demonstrations)
        return when (val result = largeLanguageApiHelper.callDeepSeek(query, promptTemplate)) {
            is ResultWrapper.Success -> ResultWrapper.Success(result.data)
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
    }

    suspend fun baselineConvertToSQL(query: String): ResultWrapper<out String> {
        val promptTemplate = preprocessingHelper.processBaselinePrompt(query)
        return when (val result = largeLanguageApiHelper.callDeepSeek(query, promptTemplate)) {
            is ResultWrapper.Success -> ResultWrapper.Success(result.data)
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
    }
}
