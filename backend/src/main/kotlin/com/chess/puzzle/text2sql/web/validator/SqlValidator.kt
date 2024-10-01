package com.chess.puzzle.text2sql.web.validator;

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import org.springframework.stereotype.Component

@Component
class SqlValidator {
    fun isValidSql(sql: String): Boolean {
        return try {
            CCJSqlParserUtil.parse(sql)
            true
        } catch (e: JSQLParserException) {
            false
        }
    }

    fun isAllowedCommand(sql: String): Boolean {
        return sql.trim().startsWith("SELECT", ignoreCase = true)
    }
}
