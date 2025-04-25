package ast.statement

import ast.AstNode
import ast.statement.expr.ident.IdentNode
import ast.statement.expr.ident.TypeNode
import ast.utils.HelpGroupNode

class FuncDeclarationNode(
    val type: TypeNode,
    val name: IdentNode,
    val params: List<ParamNode>,
    val variables: List<VariablesDeclarationNode>,
    val body: StatementNode,
) : StatementNode() {

    override fun toString(): String {
        return "f_desc"
    }

    override fun children(): List<AstNode> {
        val astNodes: MutableList<AstNode> = ArrayList()
        val groupNodes: MutableList<AstNode> = ArrayList()
        groupNodes.add(this.name)
        astNodes.add(HelpGroupNode(type.toString(), groupNodes))
        astNodes.add(HelpGroupNode("params", this.params))
        if (variables.isNotEmpty()) astNodes.add(HelpGroupNode("variables", this.variables))
        astNodes.add(this.body)
        return astNodes
    }
}