package ast.statement

import ast.AstNode
import ast.statement.expr.ExprNode
import ast.utils.HelpGroupNode
import semantic.base.TypeDesc

class TypeConvertNode() : ExprNode() {
    var expr: ExprNode? = null
    var type: TypeDesc? = null

    constructor(expr: ExprNode?, type: TypeDesc?) : this() {
        this.expr = expr
        this.type = type
        super.nodeType = type
    }

    override fun toString(): String {
        return "convert"
    }

    override fun children(): List<AstNode> {
        val astNodes: MutableList<AstNode> = ArrayList()
        val groupNodes: MutableList<AstNode> = ArrayList()
        this.expr?.let { groupNodes.add(it) }
        astNodes.add(HelpGroupNode(type.toString(), groupNodes))
        return astNodes
    }

}