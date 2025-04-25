package ast.utils

import semantic.base.IdentDesc
import semantic.base.TypeDesc

object AstBase {
    //var EMPTY_STMT: StmtListNode = StmtListNode()
    val EMPTY_IDENT: IdentDesc = IdentDesc("", TypeDesc.VOID)
}
