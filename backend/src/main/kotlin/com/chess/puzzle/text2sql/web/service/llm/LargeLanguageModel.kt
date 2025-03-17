package com.chess.puzzle.text2sql.web.service.llm

import io.ktor.client.statement.HttpResponse

/**
 * Interface defining the contract for interacting with large language models.
 *
 * Implementations of this interface provide a standardized way to call different large language
 * models, allowing for consistent interaction regardless of the specific model.
 */
interface LargeLanguageModel {
    /**
     * Calls the specified large language model with the provided query.
     *
     * This method sends a query to the large language model and awaits the response.
     *
     * @param query The input string or prompt to be processed by the language model.
     * @return A [HttpResponse] containing the response data from the model's API.
     */
    suspend fun callModel(query: String): HttpResponse
}
