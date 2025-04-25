package ast.statement

import ast.AstNode
import ast.statement.expr.ExprNode

class ForConditionNode (
    var count: AssignNode,
    var direction: String,
    var destination: ExprNode,
) : StatementNode() {

    override fun children(): List<AstNode?> {
        val astNodes: MutableList<AstNode?> = ArrayList()
        astNodes.add(this.count)
        astNodes.add(this.destination)
        return astNodes
    }

    override fun toString(): String {
        return direction
    }
}