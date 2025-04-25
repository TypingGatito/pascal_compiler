package ast

import ast.statement.FuncDeclarationNode
import ast.statement.StatementsBlockNode
import ast.statement.VariablesDeclarationNode

class ProgramNode(
    val variables: List<VariablesDeclarationNode>,
    val statements: StatementsBlockNode,
    val functions: List<FuncDeclarationNode>
) : AstNode() {

    override fun children(): List<AstNode?> {
        return functions + variables + statements
    }

    override fun toString(): String {
        return "prog"
    }

}