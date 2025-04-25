package ast.utils

import ast.statement.StatementsBlockNode
import semantic.base.IdentDesc
import semantic.base.TypeDesc

object AstBase {
    var EMPTY_STMT: StatementsBlockNode = StatementsBlockNode(emptyList())
    val EMPTY_IDENT: IdentDesc = IdentDesc("", TypeDesc.VOID)
}
