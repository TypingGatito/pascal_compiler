package ast.statement.expr

import ast.AstNode
import semantic.base.BinOp

class BinOpNode() : ExprNode() {

    var binOp: BinOp? = null
    lateinit var arg1: ExprNode
    lateinit var arg2: ExprNode

    constructor(
        binOp: String,
        exprNode1: ExprNode,
        exprNode2: ExprNode,
    ) : this() {
        this.binOp = BinOp.byValue(binOp)
        this.arg1 = exprNode1
        this.arg2 = exprNode2
    }

    override fun children(): List<AstNode> {
        return listOf<AstNode>(arg1, arg2)
    }

    override fun toString(): String {
        return binOp?.toString() ?: "not found"
    }
}