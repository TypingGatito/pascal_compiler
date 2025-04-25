package ast.statement.expr.ident

import ast.statement.expr.ExprNode

open class IdentNode(
    name: String
) : ExprNode() {
    var name: String = name.lowercase()

    override fun toString(): String {
        return name
    }

}