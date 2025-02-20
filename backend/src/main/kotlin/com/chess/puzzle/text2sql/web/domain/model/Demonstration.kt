package com.chess.puzzle.text2sql.web.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a demonstration used for prompting the large language model in the Text2SQL process.
 *
 * This class contains a natural question and an associated SQL statement, both typed as String. It
 * is used by multiple service and helper classes as part of the in-context learning process.
 * In-context learning (ICL) involves providing examples of the task (demonstrations) to the model
 * as part of the prompt. These demonstrations help the model understand the relationship between
 * natural language queries and SQL statements.
 *
 * As part of the Text2SQL process:
 * - It is fetched by [com.chess.puzzle.text2sql.web.service.helper.SentenceTransformerHelper],
 * - Then, added into a prompt template by
 *   [com.chess.puzzle.text2sql.web.service.helper.PreprocessingHelper]
 * - Finally, it used to prompt the large language model using
 *   [com.chess.puzzle.text2sql.web.service.helper.LargeLanguageApiHelper]
 *
 * @see [com.chess.puzzle.text2sql.web.entities.Puzzle] Read more about in-context learning at
 *   [IBM](https://research.ibm.com/blog/demystifying-in-context-learning-in-large-language-model)
 */
@Serializable
data class Demonstration(
    /**
     * The text of the demonstration.
     *
     * It is used to represent a natural question or a query
     *
     * @sample "Dutch Defense Middle game puzzle"
     * @sample "I want to get better at playing against Queen's Gambit"
     * @sample "Give me a hard puzzle with a knight sacrifice"
     */
    val text: String,
    /**
     * The sql statement of the demonstration
     *
     * The SQL statement is used to fetch relevant puzzles based on the [text]. It is handmade.
     *
     * @sample "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Dutch_Defense%'"
     * @sample "SELECT * FROM t_puzzle WHERE opening_tags LIKE '%Queens_Gambit%'"
     * @sample "SELECT * FROM t_puzzle WHERE rating > 1900 AND themes LIKE '%knightEndgame%'"
     * @see [com.chess.puzzle.text2sql.web.entities.Puzzle]
     */
    val sql: String,
)
