package com.chess.puzzle.text2sql.web.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents metadata associated with a search operation, containing detailed information about the
 * search query.
 *
 * This class stores information related to a search operation, including the original query string,
 * the AI model used for generating the search, the masked query string (with sensitive information
 * removed), and the SQL query generated for executing the search in the database.
 */
@Serializable
data class SearchMetadata(
    /** The original search query string provided by the user or application. */
    val query: String,

    /** The AI model used to process or generate the search query. */
    val model: ModelName,

    /**
     * The masked version of the search query string, with sensitive or critical information
     * removed.
     */
    val maskedQuery: String,

    /** The SQL query string generated for executing the search in the database. */
    val sql: String,
)
