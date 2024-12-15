package com.chess.puzzle.text2sql.web.validator

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import org.springframework.stereotype.Component

/**
 * A component responsible for validating SQL statements.
 *
 * This class provides methods to ensure that a given string is a valid SQL statement and that it
 * adheres to the allowed commands (e.g., only `SELECT` statements are permitted). This adds a layer
 * of security to prevent SQL injection and other malicious SQL commands from being executed.
 *
 * For more information on SQL injection prevention, refer to the OWASP SQL Injection Prevention
 * Cheat Sheet.
 */
@Component
class SqlValidator {

    /**
     * Validates whether the provided string is a valid SQL statement.
     *
     * This method uses the [CCJSqlParserUtil] from the JSQLParser library to parse the SQL string.
     * If the string is successfully parsed, it is considered valid; otherwise, it is invalid.
     *
     * @param sql The SQL string to validate.
     * @return `true` if the string is a valid SQL statement, `false` otherwise.
     */
    fun isValidSql(sql: String): Boolean {
        if (sql.isEmpty()) return false
        return try {
            CCJSqlParserUtil.parse(sql)
            true
        } catch (e: JSQLParserException) {
            false
        }
    }

    /**
     * Checks whether the provided SQL string is an allowed command.
     *
     * This method ensures that the SQL string starts with the `SELECT` keyword (case-insensitive).
     * Only `SELECT` statements are permitted to prevent execution of potentially harmful SQL
     * commands.
     *
     * @param sql The SQL string to validate.
     * @return `true` if the string starts with `SELECT`, `false` otherwise.
     */
    fun isAllowed(sql: String): Boolean {
        return sql.trim().startsWith("SELECT", ignoreCase = true)
    }
}
