package ast.statement.expr.ident

import semantic.base.TypeDesc

class TypeNode(
    name: String
) : IdentNode(name){

    var type: TypeDesc? = null
    init {
        initNode(name)
    }

    private fun initNode(name: String) {
        try {
            this.type = TypeDesc.fromStr(name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun toStringFull(): String {
        return toString()
    }

    override fun toString(): String {
        return name
    }

}