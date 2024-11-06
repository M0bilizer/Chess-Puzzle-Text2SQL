package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class CustomPuzzleRepositoryImpl : CustomPuzzleRepository {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    override fun executeSqlQuery(sqlCommand: String): List<Puzzle> {
        return try {
            entityManager.createNativeQuery(sqlCommand, Puzzle::class.java).resultList as List<Puzzle>
        } catch (ex: Exception) {
            throw RuntimeException("Oops")
        }
    }
}
