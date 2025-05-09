package ast.statement.expr

import ast.AstNode
import ast.statement.expr.ident.IdentNode

class FuncCallNode(
    var name: IdentNode,
    var params: List<ExprNode>,
) : ExprNode() {

    override fun children(): List<AstNode> {
        return params + name
    }

    override fun toString(): String {
        return "func: ${name}"
    }

}