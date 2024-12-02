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
    @Autowired private val sentenceTransformerService: SentenceTransformerHelper,
    @Autowired private val preprocessingService: PreprocessingHelper,
    @Autowired private val largeLanguageApiService: LargeLanguageApiHelper,
) {
    suspend fun convertToSQL(query: String): ResultWrapper<out String> {
        val demonstrations: List<Demonstration>
        when (val result = sentenceTransformerService.getSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
        val prompt = preprocessingService.processPrompt(query, demonstrations)
        return when (val result = largeLanguageApiService.callDeepSeek(prompt)) {
            is ResultWrapper.Success -> result
            else -> {
                logger.error { "oops" }
                return ResultWrapper.Error.ResponseError
            }
        }
    }
}
