package ast.statement.expr

class LiteralNode(
    var symbol: String,
) : ExprNode() {

    lateinit var value: Any

    init {
        initNode(symbol)
    }

    override fun toString(): String {
        return symbol
    }

    private fun initNode(symbol: String) {
        this.symbol = symbol
        if (symbol == "true" || symbol == "false") {
            this.value = symbol.toBoolean()
        } else if (symbol.contains("\"")) {
            this.value = symbol
        } else if (symbol.contains(".")) {
            this.value = symbol.toFloat()
        } else {
            this.value = symbol.toInt()
        }
    }

}
