package ast

import exceptions.SemanticException
import semantic.base.IdentDesc
import semantic.base.IdentScope
import semantic.base.TypeDesc
import semantic.checker.SemanticChecker


abstract class AstNode() {

    var row: Int? = null
    var col: Int? = null
    var nodeType: TypeDesc? = null
    var nodeIdent: IdentDesc? = null


    open fun children(): List<AstNode?> {
        return listOf()
    }

    open fun toStringFull(): String {
        var r = ""
        if (this.nodeIdent != null) {
            r = nodeIdent.toString()
        } else if (this.nodeType != null) {
            r = nodeType.toString()
        }

        return this.toString() + (if (r != "") " : $r" else "")
    }

    open fun tree(): List<String?> {
        val res: MutableList<String> = mutableListOf()
        res.add(toStringFull())
        val children: List<AstNode?> = children()

        for ((i, child) in children.withIndex()) {
            var ch0 = "├"
            var ch = "│"
            if (i == children.size - 1) {
                ch0 = "└"
                ch = " "
            }
            val tree = child?.tree() ?: continue

            for ((j, s) in tree.withIndex()) {
                res.add((if (j == 0) ch0 else ch) + ' ' + s)
            }
        }

        return res
    }

    fun semanticError(message: String?): SemanticException {
        return SemanticException(message!!, this.row, this.col)
    }

    @Throws(SemanticException::class)
    fun semanticCheck(checker: SemanticChecker, scope: IdentScope) {
        checker.semanticCheck(this, scope)
    }

}
