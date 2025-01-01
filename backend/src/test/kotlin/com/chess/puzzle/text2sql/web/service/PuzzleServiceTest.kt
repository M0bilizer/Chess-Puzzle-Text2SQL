import com.chess.puzzle.text2sql.web.entities.Puzzle
import com.chess.puzzle.text2sql.web.entities.ResultWrapper
import com.chess.puzzle.text2sql.web.entities.helper.GetRandomPuzzlesError
import com.chess.puzzle.text2sql.web.entities.helper.ProcessQueryError
import com.chess.puzzle.text2sql.web.repositories.PuzzleRepository
import com.chess.puzzle.text2sql.web.service.PuzzleService
import com.chess.puzzle.text2sql.web.validator.SqlValidator
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PuzzleServiceTest {

    private val puzzleRepository: PuzzleRepository = mockk()
    private val sqlValidator: SqlValidator = mockk()

    private val puzzleService = PuzzleService(puzzleRepository, sqlValidator)

    @Test
    fun `test getRandomPuzzles success`() {
        val puzzles =
            listOf(
                Puzzle(
                    id = 1,
                    puzzleId = "00sHx",
                    fen = "r3k2r/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                    moves = "e8d7 a2e6 d7d8 f7f8",
                    rating = 1760,
                    ratingDeviation = 80,
                    popularity = 83,
                    nbPlays = 72,
                    themes = "mate mateIn2 middlegame short",
                    gameUrl = "https://lichess.org/yyznGmXs/black#34",
                    openingTags = "Italian_Game Italian_Game_Classical_Variation",
                )
            )
        every { puzzleRepository.findRandomPuzzles(5) } returns puzzles

        val result = puzzleService.getRandomPuzzles(5)
        expectThat(result).isEqualTo(ResultWrapper.Success(puzzles))
    }

    @Test
    fun `test getRandomPuzzles failure`() {
        val exception = RuntimeException("Database error")
        every { puzzleRepository.findRandomPuzzles(5) } throws exception

        val result = puzzleService.getRandomPuzzles(5)
        expectThat(result)
            .isEqualTo(ResultWrapper.Failure(GetRandomPuzzlesError.Throwable(exception)))
    }

    @Test
    fun `test processQuery success`() {
        val sqlCommand = "SELECT * FROM puzzles"
        val puzzles =
            listOf(
                Puzzle(
                    id = 1,
                    puzzleId = "00sHx",
                    fen = "r3k2r/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17",
                    moves = "e8d7 a2e6 d7d8 f7f8",
                    rating = 1760,
                    ratingDeviation = 80,
                    popularity = 83,
                    nbPlays = 72,
                    themes = "mate mateIn2 middlegame short",
                    gameUrl = "https://lichess.org/yyznGmXs/black#34",
                    openingTags = "Italian_Game Italian_Game_Classical_Variation",
                )
            )
        every { sqlValidator.isValidSql(sqlCommand) } returns true
        every { sqlValidator.isAllowed(sqlCommand) } returns true
        every { puzzleRepository.executeSqlQuery(sqlCommand) } returns puzzles

        val result = puzzleService.processQuery(sqlCommand)
        expectThat(result).isEqualTo(ResultWrapper.Success(puzzles))
    }

    @Test
    fun `test processQuery invalid SQL`() {
        val sqlCommand = "INVALID SQL"
        every { sqlValidator.isValidSql(sqlCommand) } returns false
        every { sqlValidator.isAllowed(sqlCommand) } returns true

        val result = puzzleService.processQuery(sqlCommand)
        expectThat(result)
            .isEqualTo(
                ResultWrapper.Failure(
                    ProcessQueryError.ValidationError(isValid = false, isAllowed = true)
                )
            )
    }

    @Test
    fun `test processQuery not allowed SQL`() {
        val sqlCommand = "DELETE FROM puzzles"
        every { sqlValidator.isValidSql(sqlCommand) } returns true
        every { sqlValidator.isAllowed(sqlCommand) } returns false

        val result = puzzleService.processQuery(sqlCommand)
        expectThat(result)
            .isEqualTo(
                ResultWrapper.Failure(
                    ProcessQueryError.ValidationError(isValid = true, isAllowed = false)
                )
            )
    }

    @Test
    fun `test processQuery hibernate error`() {
        val sqlCommand = "SELECT * FROM puzzles"
        every { sqlValidator.isValidSql(sqlCommand) } returns true
        every { sqlValidator.isAllowed(sqlCommand) } returns true
        every { puzzleRepository.executeSqlQuery(sqlCommand) } throws
            RuntimeException("Hibernate error")

        val result = puzzleService.processQuery(sqlCommand)
        expectThat(result).isEqualTo(ResultWrapper.Failure(ProcessQueryError.HibernateError))
    }
}
