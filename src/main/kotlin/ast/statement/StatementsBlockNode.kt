package ast.statement

import ast.AstNode

class StatementsBlockNode(
    val statements: List<StatementNode>,
) : StatementNode() {

    override fun children(): List<AstNode?> {
        return statements
    }

    override fun toString(): String {
        return "..."
    }

}