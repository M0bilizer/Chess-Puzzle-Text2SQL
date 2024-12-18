package com.chess.puzzle.text2sql.web.validator

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class SqlValidatorTest {
    private val validator = SqlValidator()

    @Test
    fun `valid SQL should return true`() {
        val validSql = "SELECT * FROM t_puzzle"
        val result = validator.isValidSql(validSql)
        expectThat(result).isTrue()
    }

    @Test
    fun `SQL with prefix should return false`() {
        val invalidSql = "sql: SELECT * FROM t_puzzle"
        val result = validator.isValidSql(invalidSql)
        expectThat(result).isFalse()
    }

    @Test
    fun `SQL with suffix should return false`() {
        val invalidSql = "SELECT * FROM t_puzzle/n"
        val result = validator.isValidSql(invalidSql)
        expectThat(result).isFalse()
    }

    @Test
    fun `empty SQL should return false`() {
        val emptySql = ""
        val result = validator.isValidSql(emptySql)
        expectThat(result).isFalse()
    }

    @Test
    fun `SQL with only whitespace should return false`() {
        val whitespaceSql = "       "
        val result = validator.isValidSql(whitespaceSql)
        expectThat(result).isFalse()
    }

    @Test
    fun `SELECT should return true`() {
        val selectSQL = "SELECT * from t_puzzle"
        val result = validator.isAllowed(selectSQL)
        expectThat(result).isTrue()
    }

    @Test
    fun `UPDATE should return false`() {
        val updateSql = "UPDATE t_puzzle SET puzzle_id = '1'"
        val result = validator.isAllowed(updateSql)
        expectThat(result).isFalse()
    }

    @Test
    fun `INSERT INTO should return false`() {
        val samplePuzzle =
            "00sHx,q3k1nr/1pp1nQpp/3p4/1P2p3/4P3/B1PP1b2/B5PP/5K2 b k - 0 17,e8d7 a2e6 d7d8 f7f8,1760,80,83,72,mate mateIn2 middlegame short,https://lichess.org/yyznGmXs/black#34,Italian_Game Italian_Game_Classical_Variation"
        val insertIntoSql = "Insert INTO t_puzzle VALUES($samplePuzzle)"
        val result = validator.isAllowed(insertIntoSql)
        expectThat(result).isFalse()
    }

    @Test
    fun `DELETE FROM should return false`() {
        val deleteFromSQL = "DELETE FROM t_puzzle"
        val result = validator.isAllowed(deleteFromSQL)
        expectThat(result).isFalse()
    }

    @Test
    fun `DROP DATABASE should return false`() {
        val dropDatabaseSQL = "DROP DATABASE t_puzzle"
        val result = validator.isAllowed(dropDatabaseSQL)
        expectThat(result).isFalse()
    }

    @Test
    fun `CREATE TABLE should return false`() {
        val createTableSql = "CREATE TABLE t_table(id int)"
        val result = validator.isAllowed(createTableSql)
        expectThat(result).isFalse()
    }

    @Test
    fun `ALTER TABLE should return false`() {
        val alterTableSql = "ALTER TABLE t_table ADD field int"
        val result = validator.isAllowed(alterTableSql)
        expectThat(result).isFalse()
    }
}
