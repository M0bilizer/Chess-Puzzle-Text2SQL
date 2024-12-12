package com.chess.puzzle.text2sql.web.entities.helper

import kotlinx.serialization.Serializable

/**
 * Represents a record in the benchmark.json file used to test the Text2SQL model.
 *
 * This class is used to deserialize the json file and represent a record in the benchmark. Each
 * record will be used to test whether the Text2SQL model can effectively create a valid SQL
 * statement. It is used by [com.chess.puzzle.text2sql.web.service.BenchmarkService].
 */
@Serializable
data class BenchmarkEntry(
    /**
     * The natural question of the benchmark entry.
     *
     * A natural question which will be used to challenge the Text2SQL model. The Text2SQL model
     * should convert this to a SQL Statement.
     *
     * @sample "Find puzzles with a knight sacrifice in the endgame."
     * @sample "Give me a puzzle where the player must defend against the Sicilian Defense."
     * @sample "Show me a puzzle with a high rating and a popular theme."
     */
    var text: String
)
