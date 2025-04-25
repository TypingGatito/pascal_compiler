package semantic.base

import ast.AstNode
import ast.statement.TypeConvertNode
import ast.statement.expr.ExprNode
import exceptions.SemanticException

object SemanticBase {
    val VOID = BaseType.VOID
    val INT = BaseType.INT
    val REAL = BaseType.REAL
    val BOOL = BaseType.BOOL
    val STR = BaseType.STR

    val TYPE_CONVERTIBILITY: Map<BaseType, List<BaseType>> = mapOf(
        INT to listOf(REAL, BOOL, STR),
        REAL to listOf(STR),
        BOOL to listOf(STR)
    )

    val BIN_OP_TYPE_COMPATIBILITY: Map<BinOp, Map<List<BaseType>, BaseType>> = run {
        val map = mutableMapOf<BinOp, Map<List<BaseType>, BaseType>>()

        fun addOp(op: BinOp, vararg pairs: Pair<List<BaseType>, BaseType>) {
            map[op] = mapOf(*pairs)
        }

        addOp(BinOp.ADD,
            listOf(INT, INT) to INT,
            listOf(REAL, REAL) to REAL,
            listOf(STR, STR) to STR
        )

        addOp(BinOp.SUB,
            listOf(INT, INT) to INT,
            listOf(REAL, REAL) to REAL
        )

        addOp(BinOp.MUL,
            listOf(INT, INT) to INT,
            listOf(REAL, REAL) to REAL
        )

        addOp(BinOp.DIV,
            listOf(INT, INT) to INT,
            listOf(REAL, REAL) to REAL
        )

        addOp(BinOp.MOD,
            listOf(INT, INT) to INT,
            listOf(REAL, REAL) to REAL
        )

        addOp(BinOp.GT,
            listOf(INT, INT) to BOOL,
            listOf(REAL, REAL) to BOOL,
            listOf(STR, STR) to BOOL
        )

        addOp(BinOp.LT,
            listOf(INT, INT) to BOOL,
            listOf(REAL, REAL) to BOOL,
            listOf(STR, STR) to BOOL
        )

        addOp(BinOp.GE,
            listOf(INT, INT) to BOOL,
            listOf(REAL, REAL) to BOOL,
            listOf(STR, STR) to BOOL
        )

        addOp(BinOp.LE,
            listOf(INT, INT) to BOOL,
            listOf(REAL, REAL) to BOOL,
            listOf(STR, STR) to BOOL
        )

        addOp(BinOp.EQUALS,
            listOf(INT, INT) to BOOL,
            listOf(REAL, REAL) to BOOL,
            listOf(STR, STR) to BOOL,
            listOf(BOOL, BOOL) to BOOL
        )

        addOp(BinOp.NEQUALS,
            listOf(INT, INT) to BOOL,
            listOf(REAL, REAL) to BOOL,
            listOf(STR, STR) to BOOL,
            listOf(BOOL, BOOL) to BOOL
        )

        addOp(BinOp.BIT_AND,
            listOf(INT, INT) to INT
        )

        addOp(BinOp.BIT_OR,
            listOf(INT, INT) to INT
        )

        addOp(BinOp.LOGICAL_AND,
            listOf(BOOL, BOOL) to BOOL
        )

        addOp(BinOp.LOGICAL_OR,
            listOf(BOOL, BOOL) to BOOL
        )

        map
    }

    fun canTypeConvertTo(fromType: TypeDesc, toType: TypeDesc): Boolean {
        if (!fromType.isSimple || !toType.isSimple) {
            return false
        }
        return TYPE_CONVERTIBILITY[fromType.baseType]?.contains(toType.baseType) ?: false
    }

    @Throws(SemanticException::class)
    fun typeConvert(expr: ExprNode, type: TypeDesc): ExprNode {
        return typeConvert(expr, type, null, null)
    }

    @Throws(SemanticException::class)
    fun typeConvert(
        expr: ExprNode?,
        type: TypeDesc,
        exceptNode: AstNode? = null,
        comment: String? = null
    ): ExprNode {
        if (expr?.nodeType == null) {
            if (exceptNode != null) {
                throw exceptNode.semanticError("Тип выражения не определен")
            } else {
                throw SemanticException("")
            }
        }

        if (expr.nodeType == type) {
            return expr
        }

        if (expr.nodeType!!.isSimple && type.isSimple &&
            TYPE_CONVERTIBILITY[expr.nodeType!!.baseType]?.contains(type.baseType) == true) {
            return TypeConvertNode(expr, type)
        } else {
            val errorNode = exceptNode ?: expr
            val errorComment = comment?.takeIf { it.isNotBlank() }?.let { " ($it)" } ?: ""

            throw errorNode.semanticError(
                "Тип ${expr.nodeType}$errorComment не конвертируется в $type"
            )
        }
    }
}