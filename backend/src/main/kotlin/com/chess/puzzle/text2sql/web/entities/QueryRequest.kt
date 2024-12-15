package com.chess.puzzle.text2sql.web.entities

/**
 * Represents the user's query submitted on the frontend for the Text2SQL process.
 *
 * HTTP requests to the web application should follow this data format. This data format is also
 * used for endpoints used for debugging or benchmarking.
 *
 * @see [com.chess.puzzle.text2sql.web.controllers.Text2SqlController]
 */
data class QueryRequest(
    /**
     * The user's query for Text2SQL.
     *
     * This query is submitted by the user on the frontend and is used to generate a corresponding
     * SQL statement.
     */
    val query: String
)
