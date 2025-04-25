package semantic.base

enum class BinOp(private val value: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    EQUALS("=="),
    NEQUALS("!="),
    BIT_AND("&"),
    BIT_OR("|"),
    LOGICAL_AND("&&"),
    LOGICAL_OR("||");

    override fun toString(): String {
        return this.value
    }

    companion object {
        fun byValue(value: String): BinOp? {
            for (binOp in entries) {
                if (binOp.value == value) {
                    return binOp
                }
            }
            return null
        }
    }
}

