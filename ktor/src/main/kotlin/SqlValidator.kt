package com.chesspuzzletext2sql

import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil

fun isValidSql(sql: String): Boolean {
    if (sql.isEmpty()) return false
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
