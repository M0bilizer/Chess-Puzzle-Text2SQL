package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle

/**
 * Custom Repository interface for managing [Puzzle] entities. This interface provide the function
 * to execute a string as SQL for the [Puzzle] entity.
 *
 * For more information on Hibernate and JPA repositories, refer to the official Spring Data JPA
 * documentation.
 */
interface CustomPuzzleRepository {
    fun executeSqlQuery(sqlCommand: String): List<Puzzle>
}
