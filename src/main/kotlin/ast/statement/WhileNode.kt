package ast.statement

import ast.AstNode
import ast.statement.expr.ExprNode

class WhileNode (
    var cond: ExprNode,
    var body: StatementNode,
) : StatementNode() {

    override fun children(): List<AstNode?> {
        val astNodes: MutableList<AstNode?> = ArrayList()
        astNodes.add(this.cond)
        astNodes.add(this.body)
        return astNodes
    }

    override fun toString(): String {
        return "while"
    }
}