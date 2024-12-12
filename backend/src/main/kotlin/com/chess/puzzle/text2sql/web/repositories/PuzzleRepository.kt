package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Repository interface for managing [Puzzle] entities. This interface extends [JpaRepository] and
 * [CustomPuzzleRepository] to provide fetching random puzzle and executing a string as SQL for the
 * [Puzzle] entity.
 *
 * For more information on Hibernate and JPA repositories, refer to the official Spring Data JPA
 * documentation.
 */
@Repository
interface PuzzleRepository : JpaRepository<Puzzle, String>, CustomPuzzleRepository {

    /**
     * Retrieves a list of random [Puzzle] entities from the database.
     *
     * This method uses a native SQL query to efficiently fetch random puzzles. The query works by
     * generating a random ID and selecting puzzles with IDs greater than or equal to the random ID,
     * ensuring randomness while avoiding the performance issues of `ORDER BY RAND()` in MySQL.
     *
     * @param n The number of random puzzles to retrieve.
     * @return A list of [Puzzle] entities, or an empty list if no puzzles are found.
     * @see <a href="http://jan.kneschke.de/projects/mysql/order-by-rand/">MySQL RAND()
     *   Performance</a>
     */
    // Note: Using `ORDER BY RAND()` is very slow in MySQL. This query avoids that issue by
    // generating a random ID and selecting puzzles with IDs greater than or equal to it.
    // For more details, see: http://jan.kneschke.de/projects/mysql/order-by-rand/
    @Query(
        value =
            """
        SELECT *
        FROM t_puzzle AS r1
        JOIN (SELECT CEIL(RAND() * (SELECT MAX(id) FROM t_puzzle)) AS random_id) AS r2
        WHERE r1.id >= r2.random_id
        ORDER BY r1.id ASC
        LIMIT :n
    """,
        nativeQuery = true,
    )
    fun findRandomPuzzles(n: Int): List<Puzzle>
}
