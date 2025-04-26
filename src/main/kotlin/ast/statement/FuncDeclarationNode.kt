package ast.statement

import ast.AstNode
import ast.statement.expr.ident.IdentNode
import ast.statement.expr.ident.TypeNode
import ast.utils.AstBase
import ast.utils.HelpGroupNode
import parser.ParseException
import semantic.checker.SemanticChecker

class FuncDeclarationNode(
    var type: TypeNode,
    var name: IdentNode,
    var params: List<ParamNode>,
    var variables: List<VariablesDeclarationNode>,
    var body: StatementNode,

    ) : StatementNode() {
    lateinit var returnAssignment: FunctionReturnAssignmentNode

    init {
        when (body) {
            is AssignNode -> {
                val asNode = body as AssignNode
                body = AstBase.EMPTY_STMT
                returnAssignment = FunctionReturnAssignmentNode(
                    variable = asNode.variable,
                    value = asNode.value,
                )
            }

            is StatementsBlockNode -> {
                val bodyReal = (body as StatementsBlockNode)
                val last = bodyReal.statements.lastOrNull()
                last?.let { lastSt ->
                    if (lastSt !is AssignNode) {
                        SemanticChecker.errors.add("Функция ${name} не возвращает значение")

                    } else {
                        returnAssignment = FunctionReturnAssignmentNode(
                            variable = lastSt.variable,
                            value = lastSt.value,
                        )

                        val newStatements = bodyReal.statements.toMutableList()
                        newStatements.removeLast()
                        bodyReal.statements = newStatements
                    }
                } ?: run {
                    SemanticChecker.errors.add("Функция ${name} не возвращает значение")
                }
            }

            else -> {
                SemanticChecker.errors.add("Функция ${name} не возвращает значение")
            }
        }

        if (returnAssignment.variable.name == this.name.name
        ) {

        } else {
            SemanticChecker.errors.add("Функция ${name} не возвращает значение")
        }
    }

    override fun toString(): String {
        return "fun_declaration"
    }

    override fun children(): List<AstNode> {
        val astNodes: MutableList<AstNode> = ArrayList()
        val groupNodes: MutableList<AstNode> = ArrayList()
        groupNodes.add(this.name)
        astNodes.add(HelpGroupNode(type.toString(), groupNodes))
        astNodes.add(HelpGroupNode("params", this.params))
        if (variables.isNotEmpty()) astNodes.add(HelpGroupNode("variables", this.variables))
        astNodes.add(this.body)
        astNodes.add(this.returnAssignment)
        return astNodes
    }
}