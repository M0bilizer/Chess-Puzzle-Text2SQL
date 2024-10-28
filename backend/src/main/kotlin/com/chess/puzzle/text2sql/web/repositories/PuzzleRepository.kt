package com.chess.puzzle.text2sql.web.repositories

import com.chess.puzzle.text2sql.web.entities.Puzzle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PuzzleRepository : JpaRepository<Puzzle, String>, CustomPuzzleRepository {

    //@Query(value = "SELECT * FROM Puzzle ORDER BY RAND() LIMIT :n", nativeQuery=true)
    //http://jan.kneschke.de/projects/mysql/order-by-rand/
    @Query(value = """
        SELECT *
        FROM t_puzzle AS r1
        JOIN (SELECT CEIL(RAND() * (SELECT MAX(id) FROM t_puzzle)) AS random_id) AS r2
        WHERE r1.id >= r2.random_id
        ORDER BY r1.id ASC
        LIMIT :n
    """, nativeQuery = true)
    fun findRandomPuzzles(n: Int): List<Puzzle>
}