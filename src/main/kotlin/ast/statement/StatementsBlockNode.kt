package ast.statement

import ast.AstNode

class StatementsBlockNode(
    var statements: List<StatementNode>,
) : StatementNode() {

    override fun children(): List<AstNode> {
        return statements
    }

    override fun toString(): String {
        return "..."
    }

}