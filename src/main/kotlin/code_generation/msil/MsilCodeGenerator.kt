package code_generation.msil

import ast.AstNode
import ast.ProgramNode
import ast.statement.*
import ast.statement.expr.BinOpNode
import ast.statement.expr.FuncCallNode
import ast.statement.expr.LiteralNode
import ast.statement.expr.ident.IdentNode
import ast.statement.expr.ident.TypeNode
import code_generation.base.CodeGenBase.findVarsDecls
import code_generation.base.CodeGenerator
import code_generation.base.CodeLabel
import semantic.base.BaseType
import semantic.base.BinOp
import semantic.base.ScopeType
import semantic.base.TypeDesc
import java.util.*

class MsilCodeGenerator : CodeGenerator() {
    fun start() {
        this.add(".assembly program")
        this.add("{")
        this.add("}")
        this.add(String.format(".class public %s", PROGRAM_CLASS_NAME))
        this.add("{")
    }

    fun end() {
        this.add("}")
    }

    fun pushConst(type: BaseType?, value: Any) {
        if (type == null) return
        when (type) {
            BaseType.INT -> {
                this.add("ldc.i4", value)
            }

            BaseType.REAL -> {
                this.add("ldc.r8", value.toString())
            }

            BaseType.BOOL -> {
                this.add("ldc.i4", if (value as Boolean) 1 else 0)
            }

            BaseType.STR -> {
                this.add(String.format("ldstr %s", value))
            }

            BaseType.VOID -> {}
            BaseType.CHAR -> {}
        }
    }

    fun msilGen(node: AstNode) {
        when (node) {
            is ProgramNode -> msilGen(node)
            is AssignNode -> msilGen(node)
            is FuncDeclarationNode -> msilGen(node)
            is VariablesDeclarationNode -> msilGen(node)
            is ParamNode -> msilGen(node)
            is StatementsBlockNode -> msilGen(node)
            is BinOpNode -> msilGen(node)
            is IfNode -> msilGen(node)
            is LiteralNode -> msilGen(node)
            is TypeNode -> msilGen(node)
            is IdentNode -> msilGen(node)
            is WhileNode -> msilGen(node)
            is ForNode -> msilGen(node)
            is ForConditionNode -> msilGen(node)
            is TypeConvertNode -> msilGen(node)
            is FunctionReturnAssignmentNode -> msilGen(node)
            is FuncCallNode -> msilGen(node)
        }
    }

    private fun msilGen(node: ProgramNode) {
        this.start()
        node.variables.forEach {
            it.msilGen(this)
        }


        node.functions.forEach {
            it.msilGen(this)
        }

        this.add("")
        this.add(".method public static void Main()")
        this.add("{")
        this.add(".entrypoint")
        node.statements.msilGen(this)
        this.add("ret")
        this.add("}")
        this.end()
    }

    fun msilGen(prog: StatementsBlockNode) {
        val globalVarsDecls: List<VariablesDeclarationNode> = findVarsDecls(prog)
        for (node in globalVarsDecls) {
            for (`var` in node.identNodes) {

                if (`var`.nodeIdent!!.scope.equals(ScopeType.GLOBAL)
                    || `var`.nodeIdent!!.scope.equals(ScopeType.GLOBAL_LOCAL)
                ) {
                    this.add(
                        java.lang.String.format(
                            ".field public static %s _gv%d",
                            MSIL_TYPE_NAMES[`var`.nodeType!!.baseType], `var`.nodeIdent!!.index
                        )
                    )
                }
            }
        }

        for (stmt in prog.statements) {
            if (stmt !is FuncDeclarationNode) {
                stmt.msilGen(this)
            }
        }

    }

    private fun msilGen(node: LiteralNode) {
        node.nodeType?.let {
            this.pushConst(it.baseType, node.value)
        }
    }

    private fun msilGen(node: IdentNode) {
        if (node.nodeIdent?.scope == null || node.nodeIdent?.index == null) return
        if (node.nodeIdent!!.scope == ScopeType.LOCAL) {
            this.add("ldloc", node.nodeIdent!!.index)
        } else if (node.nodeIdent!!.scope == ScopeType.PARAM) {
            this.add("ldarg", node.nodeIdent!!.index)
        } else if (node.nodeIdent!!.scope == ScopeType.GLOBAL ||
            node.nodeIdent!!.scope == ScopeType.GLOBAL_LOCAL
        ) {
            this.add(
                java.lang.String.format(
                    "ldsfld %s %s::_gv%d",
                    MSIL_TYPE_NAMES[node.nodeIdent!!.type.baseType],
                    PROGRAM_CLASS_NAME,
                    node.nodeIdent!!.index
                )
            )
        }
    }

    private fun msilGen(node: AssignNode) {
        node.value.msilGen(this)
        val `var`: IdentNode = node.variable

        if (node.nodeIdent?.scope == null || node.nodeIdent?.index == null) return
        if (`var`.nodeIdent!!.scope == ScopeType.LOCAL) {
            this.add("stloc", `var`.nodeIdent!!.index)
        } else if (`var`.nodeIdent!!.scope == ScopeType.PARAM) {
            this.add("starg", `var`.nodeIdent!!.index)
        } else if (`var`.nodeIdent!!.scope == ScopeType.GLOBAL ||
            `var`.nodeIdent!!.scope == ScopeType.GLOBAL_LOCAL
        ) {
            this.add(
                java.lang.String.format(
                    "stsfld %s Program::_gv%d",
                    MSIL_TYPE_NAMES[`var`.nodeIdent!!.type.baseType], `var`.nodeIdent!!.index
                )
            )
        }
    }

    fun msilGen(node: VariablesDeclarationNode) {
        for (`var` in node.identNodes) {
            `var`.msilGen(this)
        }
    }

    fun msilGen(node: BinOpNode) {
        node.arg1.msilGen(this)
        node.arg2.msilGen(this)
        if (node.binOp!! == BinOp.NEQUALS) {
            if (node.arg1.nodeType!! == TypeDesc.STR) {
                this.add("call bool [mscorlib]System.String::op_Inequality(string, string)")
            } else {
                this.add("ceq")
                this.add("ldc.i4.0")
                this.add("ceq")
            }
        }
        if (node.binOp!! == BinOp.EQUALS) {
            if (node.arg1.nodeType!! == TypeDesc.STR) {
                this.add("call bool [mscorlib]System.String::op_Equality(string, string)")
            } else {
                this.add("ceq")
            }
        } else if (node.binOp!! == BinOp.GT) {
            if (node.arg1.nodeType!! == TypeDesc.STR) {
                this.add(
                    String.format(
                        "call %s class %s::compare(%s, %s)",
                        MSIL_TYPE_NAMES[BaseType.INT],
                        RUNTIME_CLASS_NAME,
                        MSIL_TYPE_NAMES[BaseType.STR], MSIL_TYPE_NAMES[BaseType.STR]
                    )
                )
                this.add("ldc.i4.0")
                this.add("cgt")
            } else {
                this.add("cgt")
            }
        } else if (node.binOp!! == BinOp.LT) {
            if (node.arg1.nodeType!! == TypeDesc.STR) {
                this.add(
                    String.format(
                        "call %s class %s::compare(%s, %s)",
                        MSIL_TYPE_NAMES[BaseType.INT],
                        RUNTIME_CLASS_NAME,
                        MSIL_TYPE_NAMES[BaseType.STR], MSIL_TYPE_NAMES[BaseType.STR]
                    )
                )
                this.add("ldc.i4.0")
                this.add("clt")
            } else {
                this.add("clt")
            }
        } else if (node.binOp!! == BinOp.GE) {
            if (node.arg1.nodeType!! == TypeDesc.STR) {
                this.add(
                    String.format(
                        "call %s class %s::compare(%s, %s)",
                        MSIL_TYPE_NAMES[BaseType.INT],
                        RUNTIME_CLASS_NAME,
                        MSIL_TYPE_NAMES[BaseType.STR], MSIL_TYPE_NAMES[BaseType.STR]
                    )
                )
                this.add("ldc.i4", "-1")
                this.add("cgt")
            } else {
                this.add("clt")
                this.add("ldc.i4.0")
                this.add("ceq")
            }
        } else if (node.binOp!! == BinOp.LE) {
            if (node.arg1.nodeType!! == TypeDesc.STR) {
                this.add(
                    String.format(
                        "call %s class %s::compare(%s, %s)",
                        MSIL_TYPE_NAMES[BaseType.INT],
                        RUNTIME_CLASS_NAME,
                        MSIL_TYPE_NAMES[BaseType.STR], MSIL_TYPE_NAMES[BaseType.STR]
                    )
                )
                this.add("ldc.i4.1")
                this.add("clt")
            } else {
                this.add("cgt")
                this.add("ldc.i4.0")
                this.add("ceq")
            }
        } else if (node.binOp!!.equals(BinOp.ADD)) {
            if (node.arg1.nodeType!!.equals(TypeDesc.STR)) {
                this.add(
                    String.format(
                        "call %s class %s::concat(%s, %s)",
                        MSIL_TYPE_NAMES[BaseType.STR],
                        RUNTIME_CLASS_NAME,
                        MSIL_TYPE_NAMES[BaseType.STR], MSIL_TYPE_NAMES[BaseType.STR]
                    )
                )
            } else {
                this.add("add")
            }
        } else if (node.binOp!!.equals(BinOp.SUB)) {
            this.add("sub")
        } else if (node.binOp!!.equals(BinOp.MUL)) {
            this.add("mul")
        } else if (node.binOp!!.equals(BinOp.DIV)) {
            this.add("div")
        } else if (node.binOp!!.equals(BinOp.MOD)) {
            this.add("rem")
        } else if (node.binOp!!.equals(BinOp.LOGICAL_AND)) {
            this.add("and")
        } else if (node.binOp!!.equals(BinOp.LOGICAL_OR)) {
            this.add("or")
        } else if (node.binOp!!.equals(BinOp.BIT_AND)) {
            this.add("and")
        } else if (node.binOp!!.equals(BinOp.BIT_OR)) {
            this.add("or")
        }
    }

    private fun msilGen(node: TypeConvertNode) {
        node.expr!!.msilGen(this)
        if (node.nodeType!!.baseType!!.equals(BaseType.REAL)
            && node.expr!!.nodeType!!.baseType!!.equals(BaseType.INT)
        ) {
            this.add("conv.r8")
        } else if (node.nodeType!!.baseType!!.equals(BaseType.BOOL)
            && node.expr!!.nodeType!!.baseType!!.equals(BaseType.INT)
        ) {
            this.add("ldc.i4.0")
            this.add("ceq")
            this.add("ldc.i4.0")
            this.add("ceq")
        } else {
            this.add(
                String.format(
                    "call %s class %s::convert(%s)",
                    MSIL_TYPE_NAMES[node.nodeType!!.baseType],
                    RUNTIME_CLASS_NAME,
                    MSIL_TYPE_NAMES[node.expr!!.nodeType!!.baseType]
                )
            )
        }
    }

    fun msilGen(node: FuncCallNode) {
        val paramTypes = StringJoiner(", ")
        for (param in node.params) {
            param.msilGen(this)
            paramTypes.add(MSIL_TYPE_NAMES[param.nodeType!!.baseType])
        }
        val className = if (node.name.nodeIdent!!.builtIn) RUNTIME_CLASS_NAME else PROGRAM_CLASS_NAME
        this.add(
            java.lang.String.format(
                "call %s class %s::%s(%s)",
                MSIL_TYPE_NAMES[node.nodeType!!.baseType],
                className, node.name.name, paramTypes.toString()
            )
        )
    }

    fun msilGen(node: FunctionReturnAssignmentNode) {
        node.value.msilGen(this)
        this.add("ret")
    }

    fun msilGen(node: IfNode) {
        val elseLabel: CodeLabel = CodeLabel()
        val endLabel: CodeLabel = CodeLabel()
        node.cond.msilGen(this)
        this.add("brfalse", elseLabel)
        node.thenStmt.msilGen(this)
        this.add("br", endLabel)
        this.add(elseLabel)
        node.elseStmt?.msilGen(this)
        this.add(endLabel)
    }

    fun msilGen(node: WhileNode) {
        val startLabel: CodeLabel = CodeLabel()
        val endLabel: CodeLabel = CodeLabel()
        this.add(startLabel)
        node.cond.msilGen(this)
        this.add("brfalse", endLabel)
        node.body.msilGen(this)
        this.add("br", startLabel)
        this.add(endLabel)
    }

    private fun msilGen(node: ForConditionNode) {
        node.count.variable.msilGen(this)

        node.destination.msilGen(this)

        when (node.direction) {
            "to" -> {
                this.add("bge")
            }

            "downto" -> {
                this.add("ble")
            }

            else -> throw IllegalArgumentException("Unknown for-loop direction: ${node.direction}")
        }
    }

    fun msilGen(node: ForNode) {
        val startLabel = CodeLabel()
        val endLabel = CodeLabel()

        node.cond.count.msilGen(this)

        this.add(startLabel)

        node.cond.msilGen(this)
        this.add("brtrue", endLabel)

        node.body.msilGen(this)

        node.cond.count.variable.msilGen(this)
        this.add("ldc.i4.1")
        when (node.cond.direction) {
            "to" -> this.add("add")
            "downto" -> this.add("sub")
        }
        node.cond.count.msilGen(this)

        this.add("br", startLabel)

        this.add(endLabel)
    }

    fun msilGen(node: FuncDeclarationNode) {
        // Генерация сигнатуры метода
        val params = StringJoiner(", ")
        for (param in node.params) {
            params.add(
                "%s %s".format(
                    MSIL_TYPE_NAMES[param.type.type!!.baseType],
                    param.name.name
                )
            )
        }

        this.add(
            ".method public static %s %s(%s) cil managed".format(
                MSIL_TYPE_NAMES[node.type.type!!.baseType],
                node.name.toString(),
                params
            )
        )

        this.add("{")

        val localVars = StringBuilder(".locals init (")
        var varCount = 0

        for ((index, param) in node.params.withIndex()) {
            if (varCount > 0) localVars.append(", ")
            localVars.append(
                "%s %s".format(
                    MSIL_TYPE_NAMES[param.type.type!!.baseType],
                    param.name.name
                )
            )
            varCount++
        }

        for (varsDecl in node.variables) {
            for (variable in varsDecl.identNodes) {
                if (varCount > 0) localVars.append(", ")
                localVars.append(
                    "%s _v%d".format(
                        MSIL_TYPE_NAMES[variable.nodeType!!.baseType],
                        variable.nodeIdent!!.index
                    )
                )
                varCount++
            }
        }

        localVars.append(")")
        if (varCount > 0) {
            this.add(localVars.toString())
        }

        node.body.msilGen(this)

        node.returnAssignment.msilGen(this)

        this.add("}")
    }

    companion object {
        private const val RUNTIME_CLASS_NAME = "CompilerDemo.Runtime"
        private const val PROGRAM_CLASS_NAME = "Program"

        private val MSIL_TYPE_NAMES: HashMap<BaseType, String> = HashMap<BaseType, String>()

        init {
            MSIL_TYPE_NAMES[BaseType.VOID] = "void"
            MSIL_TYPE_NAMES[BaseType.INT] = "int32"
            MSIL_TYPE_NAMES[BaseType.REAL] = "float64"
            MSIL_TYPE_NAMES[BaseType.BOOL] = "bool"
            MSIL_TYPE_NAMES[BaseType.STR] = "string"
        }
    }
}