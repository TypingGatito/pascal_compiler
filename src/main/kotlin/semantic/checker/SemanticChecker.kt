package semantic.checker

import ast.AstNode
import ast.ProgramNode
import ast.statement.*
import ast.statement.expr.BinOpNode
import ast.statement.expr.ExprNode
import ast.statement.expr.FuncCallNode
import ast.statement.expr.LiteralNode
import ast.statement.expr.ident.IdentNode
import ast.statement.expr.ident.TypeNode
import ast.utils.AstBase
import exceptions.SemanticException
import parser.ParseException
import parser.Parser.parse
import semantic.base.*
import semantic.base.SemanticBase.BIN_OP_TYPE_COMPATIBILITY
import semantic.base.SemanticBase.TYPE_CONVERTIBILITY
import semantic.base.SemanticBase.typeConvert

class SemanticChecker {
    companion object {
        private const val BUILT_IN_OBJECTS: String = "" +
                "function println(): integer;" +
                "begin " +
                "println:=11;" +
                "end;" +
                "begin end."


        @Throws(SemanticException::class, ParseException::class)
        fun prepareGlobalScope(): IdentScope {
            val prog: AstNode = parse(BUILT_IN_OBJECTS)
            val checker = SemanticChecker()
            val scope = IdentScope()
            checker.semanticCheck(prog, scope)
            for ((_, value) in scope.idents.entries) {
                value.builtIn = true
            }
            scope.varIndex = 0
            return scope
        }
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: AstNode, scope: IdentScope) {
        when (node) {
            is ProgramNode -> semanticCheck(node, scope)
            is AssignNode -> semanticCheck(node, scope)
            is FuncDeclarationNode -> semanticCheck(node, scope)
            is VariablesDeclarationNode -> semanticCheck(node, scope)
            is ParamNode -> semanticCheck(node, scope)
            is StatementsBlockNode -> semanticCheck(node, scope)
            is BinOpNode -> semanticCheck(node, scope)
            is IfNode -> semanticCheck(node, scope)
            is LiteralNode -> semanticCheck(node, scope)
            is TypeNode -> semanticCheck(node, scope)
            is IdentNode -> semanticCheck(node, scope)
            is WhileNode -> semanticCheck(node, scope)
            is ForNode -> semanticCheck(node, scope)
            is ForConditionNode -> semanticCheck(node, scope)
            is FuncCallNode -> semanticCheck(node, scope)
        }
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: AssignNode, scope: IdentScope) {
        node.variable.semanticCheck(this, scope)
        node.value.semanticCheck(this, scope)
        node.value = typeConvert(node.value, node.variable.nodeType!!, node, "присваемое значение")
        node.nodeType = node.variable.nodeType
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: TypeNode, scope: IdentScope) {
        if (node.type == null) {
            throw node.semanticError(java.lang.String.format("Неизвестный тип %s", node.name))
        }
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: IdentNode, scope: IdentScope) {
        val ident = scope.getIdent(node.name)
            ?: throw node.semanticError(java.lang.String.format("Идентификатор %s не найден", node.name))
        node.nodeType = ident.type
        node.nodeIdent = ident
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: FuncCallNode, scope: IdentScope) {
        val func = scope.getIdent(node.name.name)
            ?: throw node.semanticError(java.lang.String.format("Функция %s не найдена", node.name.name))
        if (!func.type.isFunc) {
            throw node.semanticError(java.lang.String.format("Идентификатор %s не является функцией", func.name))
        }
        if (func.type.params?.size != node.params.size) {
            throw node.semanticError(
                java.lang.String.format(
                    "Кол-во аргументов %s не совпадает (ожидалось %d, передано %d)",
                    func.name, func.type.params?.size, node.params.size
                )
            )
        }
        val params: MutableList<ExprNode> = mutableListOf()

        var error = false
        val declParamsStr = StringBuilder()
        val factParamsStr = StringBuilder()
        for (i in 0 until node.params.size) {
            val param: ExprNode = node.params[i]
            param.semanticCheck(this, scope)
            if (declParamsStr.isNotEmpty()) {
                declParamsStr.append(", ")
            }
            declParamsStr.append(func.type.params!![i].toString())
            if (factParamsStr.isNotEmpty()) {
                factParamsStr.append(", ")
            }
            factParamsStr.append(param.nodeType.toString())
            try {
                params.add(typeConvert(param, func.type.params!![i]!!))
            } catch (e: Exception) {
                error = true
            }
        }
        if (error) {
            throw node.semanticError(
                java.lang.String.format(
                    "Фактические типы (%s) аргументов функции %s не совпадают" +
                            "с формальными (%s) и не приводимы",
                    factParamsStr, func.name, declParamsStr
                )
            )
        } else {
            node.params = params.toList()
            node.name.nodeType = func.type
            node.name.nodeIdent = func
            node.nodeType = func.type.returnType
        }
    }


    @Throws(SemanticException::class)
    fun semanticCheck(node: ProgramNode, scope: IdentScope) {
        for (stmt in node.variables) {
            stmt.semanticCheck(this, scope)
        }
        for (stmt in node.functions) {
            stmt.semanticCheck(this, scope)
        }
        node.statements.semanticCheck(this, scope)
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: FuncDeclarationNode, scope: IdentScope) {
        if (scope.func != null) {
            throw node.semanticError(
                java.lang.String.format(
                    "Объявление функции (%s) внутри другой функции не поддерживается",
                    node.name.name
                )
            )
        }

        node.type.semanticCheck(this, scope)
        val childScope = IdentScope(scope)

        childScope.func = AstBase.EMPTY_IDENT
        val params: MutableList<TypeDesc?> = ArrayList()

        for (param in node.params) {
            param.semanticCheck(this, childScope)
            param.nodeIdent = childScope.getIdent((param.name).name)
            params.add(param.type.type)
        }

        for (variable in node.variables) {
            variable.semanticCheck(this, childScope)
        }

        val type = TypeDesc(null, node.type.type, params)
        val funcIdent = IdentDesc(node.name.name, type)
        childScope.func = funcIdent
        node.name.nodeType = type
        try {
            node.name.nodeIdent = scope.currGlobal().addIdent(funcIdent)
        } catch (e: SemanticException) {
            throw node.name
                .semanticError(java.lang.String.format("Повторное объявление функции %s", node.name.name))
        }
        node.body.semanticCheck(this, childScope)

        node.returnAssignment.variable.semanticCheck(this, childScope)
        node.returnAssignment.value.semanticCheck(this, childScope)
        SemanticBase.funcReturnCheck(node.returnAssignment)
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: VariablesDeclarationNode, scope: IdentScope) {
        node.type.semanticCheck(this, scope)
        for (variable in node.identNodes) {
            try {
                scope.addIdent(IdentDesc(variable.name, node.type.type!!))
            } catch (e: SemanticException) {
                throw variable.semanticError(e.message)
            }
            variable.semanticCheck(this, scope)
        }
        //node.nodeType = TypeDesc.VOID
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: ParamNode, scope: IdentScope) {
        node.type.semanticCheck(this, scope)
        node.name.nodeType = node.type.type
        try {
            node.name.nodeIdent =
                scope.addIdent(
                    IdentDesc(node.name.name, node.type.type!!, ScopeType.PARAM, 0)
                )

        } catch (e: SemanticException) {
            throw node.name
                .semanticError(java.lang.String.format("Параметр %s уже объявлен", node.name.name))
        }
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: StatementsBlockNode, scope: IdentScope) {

        val childScope = IdentScope(scope)

        for (stmt in node.statements) {
            stmt.semanticCheck(this, childScope)
        }
        node.nodeType = TypeDesc.VOID
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: IfNode, scope: IdentScope) {
        node.cond.semanticCheck(this, scope)
        node.cond = (typeConvert(node.cond, TypeDesc.BOOL, null, "условие"))
        node.thenStmt.semanticCheck(this, IdentScope(scope))

        node.elseStmt?.semanticCheck(this, IdentScope(scope))
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: LiteralNode, scope: IdentScope) {
        when (node.value) {
            is Boolean -> {
                node.nodeType = TypeDesc.BOOL
            }

            is Int -> {
                node.nodeType = (TypeDesc.INT)
            }

            is Float -> {
                node.nodeType = (TypeDesc.FLOAT)
            }

            is String -> {
                node.nodeType = (TypeDesc.STR)
            }

            else -> {
                throw node.semanticError(
                    java.lang.String.format(
                        "Неизвестный тип %s для %s",
                        node.value.javaClass.toString(), node.value.toString()
                    )
                )
            }
        }
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: WhileNode, scope: IdentScope) {
        node.cond.semanticCheck(this, scope)
        node.cond = typeConvert(node.cond, TypeDesc.BOOL, null, "условие")
        node.body.semanticCheck(this, IdentScope(scope))
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: ForNode, scope: IdentScope) {
        val childScope = IdentScope(scope)

        node.body.semanticCheck(this, childScope)
        node.cond.semanticCheck(this, childScope)
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: ForConditionNode, scope: IdentScope) {
        val childScope = IdentScope(scope)

        node.count.semanticCheck(this, childScope)
        node.destination.semanticCheck(this, childScope)
    }

    @Throws(SemanticException::class)
    fun semanticCheck(node: BinOpNode, scope: IdentScope) {
        node.arg1.semanticCheck(this, scope)
        node.arg2.semanticCheck(this, scope)

        if (node.arg1.nodeType!!.isSimple || node.arg2.nodeType!!.isSimple) {
            val compatibility = BIN_OP_TYPE_COMPATIBILITY[node.binOp] ?: emptyMap()
            var argsTypes: MutableList<BaseType> = mutableListOf()
            argsTypes.add(node.arg1.nodeType!!.baseType!!)
            argsTypes.add(node.arg2.nodeType!!.baseType!!)
            if (compatibility.containsKey(argsTypes)) {
                node.nodeType = TypeDesc.fromBaseType(compatibility[argsTypes]!!)
                return
            }
            if (TYPE_CONVERTIBILITY.containsKey(node.arg1.nodeType!!.baseType)) {
                for (arg2_type in TYPE_CONVERTIBILITY[node.arg2.nodeType!!.baseType]!!) {
                    argsTypes = mutableListOf()
                    argsTypes.add(node.arg1.nodeType!!.baseType!!)
                    argsTypes.add(arg2_type)
                    if (compatibility.containsKey(argsTypes)) {
                        node.arg2 = typeConvert(node.arg2, TypeDesc.fromBaseType(arg2_type))
                        node.nodeType = TypeDesc.fromBaseType(compatibility[argsTypes]!!)
                        return
                    }
                }
            }
            if (TYPE_CONVERTIBILITY.containsKey(node.arg1.nodeType!!.baseType)) {
                for (arg1_type in TYPE_CONVERTIBILITY[node.arg1.nodeType!!.baseType]!!) {
                    argsTypes = mutableListOf()
                    argsTypes.add(arg1_type)
                    argsTypes.add(node.arg1.nodeType!!.baseType!!)
                    if (compatibility.containsKey(argsTypes)) {
                        node.arg1 = typeConvert(node.arg1, TypeDesc.fromBaseType(arg1_type))
                        node.nodeType = TypeDesc.fromBaseType(compatibility[argsTypes]!!)
                        return
                    }
                }
            }
        }
        throw node.semanticError(
            java.lang.String.format(
                "Оператор %s не применим к типам (%s, %s)",
                node.binOp.toString(),
                node.arg1.nodeType.toString(),
                node.arg2.nodeType.toString()
            )
        )
    }

}