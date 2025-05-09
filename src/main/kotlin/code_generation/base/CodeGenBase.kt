package code_generation.base

import ast.AstNode
import ast.statement.VariablesDeclarationNode
import semantic.base.BaseType

object CodeGenBase {
    var DEFAULT_TYPE_VALUES: HashMap<BaseType, Any> = HashMap<BaseType, Any>()

    init {
        DEFAULT_TYPE_VALUES[BaseType.INT] = 0
        DEFAULT_TYPE_VALUES[BaseType.REAL] = 0.0
        DEFAULT_TYPE_VALUES[BaseType.BOOL] = false
        DEFAULT_TYPE_VALUES[BaseType.STR] = ""
    }

    fun findVarsDecls(node: AstNode): List<VariablesDeclarationNode> {
        val varsNodes: MutableList<VariablesDeclarationNode> = ArrayList<VariablesDeclarationNode>()
        find(node, varsNodes)
        return varsNodes
    }

    private fun find(node: AstNode, varsNodes: MutableList<VariablesDeclarationNode>) {
        for (n in node.children()) {
            if (n is VariablesDeclarationNode) {
                varsNodes.add(n as VariablesDeclarationNode)
            } else {
                find(n, varsNodes)
            }
        }
    }
}