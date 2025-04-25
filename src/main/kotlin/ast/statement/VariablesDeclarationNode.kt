package ast.statement

import ast.AstNode
import ast.statement.expr.ident.IdentNode
import ast.statement.expr.ident.TypeNode

class VariablesDeclarationNode(
    var identNodes: List<IdentNode>,
    var type: TypeNode,
) : StatementNode() {

    override fun children(): List<AstNode?> {
        return identNodes + type
    }

    override fun toString(): String {
        return "var"
    }
}