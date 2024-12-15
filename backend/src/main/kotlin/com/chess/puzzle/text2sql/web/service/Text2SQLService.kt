package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.entities.Demonstration
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.CustomError
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service class for handling the Text2SQL process.
 *
 * This service converts natural language text into SQL queries by leveraging:
 * - **Similar Demonstrations**: Finding similar examples ([Demonstration]) using
 *   [SentenceTransformerHelper].
 * - **Schema Masking**: Preprocessing the prompt template using [PreprocessingHelper].
 * - **Large Language Model (LLM)**: Calling the LLM API using [LargeLanguageApiHelper].
 *
 * The service provides three main methods for converting text to SQL:
 * - [convertToSQL]: Full Text2SQL process with schema masking and similar demonstrations.
 * - [partialConvertToSQL]: Text2SQL process without schema masking, for benchmarking.
 * - [baselineConvertToSQL]: Text2SQL process without schema masking or similar demonstrations, for
 *   benchmarking.
 */
@Service
class Text2SQLService(
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val preprocessingHelper: PreprocessingHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
) {
    /**
     * Converts the given natural language query into an SQL query using the full Text2SQL process.
     *
     * The process involves:
     * 1. Finding similar demonstrations using [SentenceTransformerHelper].
     * 2. Preprocessing the prompt template with the query and demonstrations using
     *    [PreprocessingHelper].
     * 3. Calling the LLM API to generate the SQL query using [LargeLanguageApiHelper].
     *
     * @param query The natural language query to convert to SQL.
     * @return A [ResultWrapper] containing the generated SQL query if successful, or an error if
     *   the process fails.
     */
    suspend fun convertToSQL(query: String): ResultWrapper<String, CustomError> {
        val demonstrations: List<Demonstration>
        val promptTemplate: String
        val sql: String
        when (val result = sentenceTransformerHelper.getSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = preprocessingHelper.processPrompt(query, demonstrations)) {
            is ResultWrapper.Success -> promptTemplate = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = largeLanguageApiHelper.callDeepSeek(query, promptTemplate)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }

    /**
     * Converts the given natural language query into an SQL query without schema masking.
     *
     * This method is used for benchmarking purposes. The process involves:
     * 1. Finding similar demonstrations (without schema masking) using [SentenceTransformerHelper].
     * 2. Preprocessing the prompt template with the query and demonstrations using
     *    [PreprocessingHelper].
     * 3. Calling the LLM API to generate the SQL query using [LargeLanguageApiHelper].
     *
     * @param query The natural language query to convert to SQL.
     * @return A [ResultWrapper] containing the generated SQL query if successful, or an error if
     *   the process fails.
     */
    suspend fun partialConvertToSQL(query: String): ResultWrapper<String, CustomError> {
        val demonstrations: List<Demonstration>
        val promptTemplate: String
        val sql: String
        when (val result = sentenceTransformerHelper.getPartialSimilarDemonstration(query)) {
            is ResultWrapper.Success -> demonstrations = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = preprocessingHelper.processPrompt(query, demonstrations)) {
            is ResultWrapper.Success -> promptTemplate = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = largeLanguageApiHelper.callDeepSeek(query, promptTemplate)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }

    /**
     * Converts the given natural language query into an SQL query without schema masking or similar
     * demonstrations.
     *
     * This method is used for benchmarking purposes. The process involves:
     * 1. Preprocessing the prompt template with the query (without demonstrations) using
     *    [PreprocessingHelper].
     * 2. Calling the LLM API to generate the SQL query using [LargeLanguageApiHelper].
     *
     * @param query The natural language query to convert to SQL.
     * @return A [ResultWrapper] containing the generated SQL query if successful, or an error if
     *   the process fails.
     */
    suspend fun baselineConvertToSQL(query: String): ResultWrapper<String, CustomError> {
        val promptTemplate: String
        val sql: String
        when (val result = preprocessingHelper.processBaselinePrompt(query)) {
            is ResultWrapper.Success -> promptTemplate = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        when (val result = largeLanguageApiHelper.callDeepSeek(query, promptTemplate)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }
}
