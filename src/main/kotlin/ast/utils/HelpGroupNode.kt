package ast.utils

import ast.AstNode

class HelpGroupNode(
    var name: String,
    val children: List<AstNode>,
) : AstNode() {

    override fun toString(): String {
        return this.name
    }

    override fun children(): List<AstNode> {
        return this.children
    }


}