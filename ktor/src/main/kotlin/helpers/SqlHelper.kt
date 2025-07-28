package com.chesspuzzletext2sql.helpers

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil

fun isValidSql(sql: String): Boolean {
    return try {
        CCJSqlParserUtil.parse(sql)
        true
    } catch (e: JSQLParserException) {
        false
    }
}

fun isAllowed(sql: String): Boolean {
    return sql.trim().startsWith("SELECT", ignoreCase = true)
}
