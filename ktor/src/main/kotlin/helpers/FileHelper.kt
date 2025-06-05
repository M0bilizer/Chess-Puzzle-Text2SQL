package com.chesspuzzletext2sql.helpers

fun readMdFromResources(path: String): String? = object {}.javaClass.getResource(path)?.readText()
