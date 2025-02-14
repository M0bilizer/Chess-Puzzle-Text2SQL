package com.chess.puzzle.text2sql.web.service

import com.chess.puzzle.text2sql.web.config.FilePaths
import com.chess.puzzle.text2sql.web.domain.model.Demonstration
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant
import com.chess.puzzle.text2sql.web.domain.model.ModelVariant.*
import com.chess.puzzle.text2sql.web.domain.model.ResultWrapper
import com.chess.puzzle.text2sql.web.error.SystemError
import com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper
import com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper
import com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Service class for converting natural language queries into SQL queries.
 *
 * This service handles the conversion process by:
 * - Loading prompt templates.
 * - Fetching similar demonstrations.
 * - Preprocessing the prompt with the user query and demonstrations.
 * - Calling the DeepSeek API to generate the SQL query.
 *
 * The service supports three models: Full, Partial, and Baseline.
 *
 * @property filePaths Configuration for file paths used in the service.
 * @property fileLoaderService Service for loading files from the classpath.
 * @property sentenceTransformerHelper Service for fetching similar demonstrations.
 * @property preprocessingHelper Service for preprocessing the prompt.
 * @property largeLanguageApiHelper Service for interacting with the DeepSeek API.
 */
@Service
class Text2SQLService(
    @Autowired private val filePaths: FilePaths,
    @Autowired private val fileLoaderService: FileLoaderService,
    @Autowired private val sentenceTransformerHelper: SentenceTransformerHelper,
    @Autowired private val preprocessingHelper: PreprocessingHelper,
    @Autowired private val largeLanguageApiHelper: LargeLanguageApiHelper,
) {

    /**
     * Converts a natural language query into an SQL query based on the specified model.
     *
     * @param query The natural language query to convert.
     * @param modelVariant The model to use for the conversion (Full, Partial, or Baseline).
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    suspend fun convertToSQL(
        query: String,
        modelVariant: ModelVariant,
    ): ResultWrapper<String, SystemError> {
        return when (modelVariant) {
            Full -> full(query)
            Partial -> partial(query)
            Baseline -> baseline(query)
        }
    }

    /**
     * Converts a natural language query into an SQL query using the Full model.
     *
     * The Full model:
     * 1. Loads the prompt template.
     * 2. Fetches similar demonstrations.
     * 3. Preprocesses the prompt with the query and demonstrations.
     * 4. Calls the DeepSeek API to generate the SQL query.
     *
     * @param query The natural language query to convert.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    private suspend fun full(query: String): ResultWrapper<String, SystemError> {
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
        when (val result = largeLanguageApiHelper.callDeepSeek(processedPrompt)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }

    /**
     * Converts a natural language query into an SQL query using the Partial model.
     *
     * The Partial model:
     * 1. Loads the prompt template.
     * 2. Fetches partial similar demonstrations.
     * 3. Preprocesses the prompt with the query and demonstrations.
     * 4. Calls the DeepSeek API to generate the SQL query.
     *
     * @param query The natural language query to convert.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    private suspend fun partial(query: String): ResultWrapper<String, SystemError> {
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
        when (val result = largeLanguageApiHelper.callDeepSeek(processedPrompt)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }

    /**
     * Converts a natural language query into an SQL query using the Baseline model.
     *
     * The Baseline model:
     * 1. Loads the prompt template.
     * 2. Preprocesses the prompt with the query (no demonstrations).
     * 3. Calls the DeepSeek API to generate the SQL query.
     *
     * @param query The natural language query to convert.
     * @return A [ResultWrapper] containing the SQL query or an error.
     */
    private suspend fun baseline(query: String): ResultWrapper<String, SystemError> {
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
        when (val result = largeLanguageApiHelper.callDeepSeek(processedPrompt)) {
            is ResultWrapper.Success -> sql = result.data
            is ResultWrapper.Failure -> return ResultWrapper.Failure(result.error)
        }
        return ResultWrapper.Success(sql)
    }
}
