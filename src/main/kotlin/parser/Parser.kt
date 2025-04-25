package parser

import ast.AstNode
import java.io.ByteArrayInputStream
import java.io.InputStream

object Parser {
    private var parser: PascalCompiler? = null

    fun parse(programFile: String): AstNode {
        val `is`: InputStream = ByteArrayInputStream(programFile.toByteArray())
        if (parser == null) {
            parser = PascalCompiler(`is`)
        } else {
            parser!!.ReInit(`is`)
        }

        return parser!!.start()
    }
}
