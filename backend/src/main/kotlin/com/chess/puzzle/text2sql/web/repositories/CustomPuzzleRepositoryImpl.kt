package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

/**
 * Implementation of the [CustomPuzzleRepository] interface for managing [Puzzle] entities. This
 * class provides custom functionality, such as executing raw SQL queries, to interact with the
 * [Puzzle] table in the database.
 *
 * For more information on custom repository implementations, refer to the official Spring Data JPA
 * documentation.
 */
@Repository
class CustomPuzzleRepositoryImpl : CustomPuzzleRepository {

    /**
     * The [EntityManager] instance used to interact with the persistence context. This is injected
     * by Spring and is used to execute native SQL queries.
     */
    @PersistenceContext private lateinit var entityManager: EntityManager

    /**
     * Executes a raw SQL query on the [Puzzle] table and returns the results as a list of [Puzzle]
     * entities.
     *
     * This method allows executing arbitrary SQL commands on the database. It is important to
     * ensure that the provided SQL command is valid and does not introduce security vulnerabilities
     * (e.g., SQL injection).
     *
     * @param sqlCommand The raw SQL command to execute.
     * @return A list of [Puzzle] entities resulting from the query, or an empty list if no results
     *   are found.
     * @throws RuntimeException If the [sqlCommand] is not a valid SQL statement or if an error
     *   occurs during execution.
     */
    // WARNING: This method allows executing arbitrary SQL commands. Ensure that the input
    // is sanitized to prevent SQL injection attacks.
    @Transactional
    @Suppress("UNCHECKED_CAST")
    override fun executeSqlQuery(sqlCommand: String): List<Puzzle> {
        return try {
            entityManager.createNativeQuery(sqlCommand, Puzzle::class.java).resultList
                as List<Puzzle>
        } catch (ex: Exception) {
            throw RuntimeException("Oops")
        }
    }
}
