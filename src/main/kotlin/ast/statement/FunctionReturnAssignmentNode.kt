package ast.statement

import ast.AstNode
import ast.statement.expr.ExprNode
import ast.statement.expr.ident.IdentNode

class FunctionReturnAssignmentNode(
    var variable: IdentNode,
    var value: ExprNode,
) : StatementNode() {

    override fun children(): List<AstNode> {
        return listOf<AstNode>(variable, value)
    }

    override fun toString(): String {
        return "return"
    }

}