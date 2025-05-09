package ast.statement

import ast.AstNode
import ast.statement.expr.ExprNode

class IfNode (
    var cond: ExprNode,
    var thenStmt: StatementNode,
    var elseStmt: StatementNode?,
) : StatementNode() {

    override fun children(): List<AstNode> {
        val astNodes: MutableList<AstNode> = ArrayList()
        astNodes.add(this.cond)
        astNodes.add(this.thenStmt)
        this.elseStmt?.let { astNodes.add(it) }
        return astNodes
    }

    override fun toString(): String {
        return "if"
    }
}