package com.chesspuzzletext2sql.helpers

import java.io.File

fun readMarkdownFileBasic(path: String): String {
    return File(path).readText(Charsets.UTF_8)
}
