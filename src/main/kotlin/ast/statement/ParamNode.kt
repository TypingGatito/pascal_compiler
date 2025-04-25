package ast.statement

import ast.AstNode
import ast.statement.expr.ident.IdentNode
import ast.statement.expr.ident.TypeNode

class ParamNode(
    var type: TypeNode,
    val name: IdentNode,
) : StatementNode() {
    override fun toString(): String {
        return this.type.toString()
    }

    override fun children(): List<AstNode> {
        val astNodes: MutableList<AstNode> = ArrayList()
        astNodes.add(this.name)
        return astNodes
    }
}