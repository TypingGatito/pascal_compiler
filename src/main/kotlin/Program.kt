import ast.AstNode
import code_generation.msil.MsilCodeGenerator
import parser.Parser.parse
import printer.Printer
import printer.Printer.printTree
import semantic.base.IdentScope
import semantic.checker.SemanticChecker
import kotlin.system.exitProcess

object Program {

    fun execute(
        programFile: String,
        showBaseTree: Boolean,
        showSemanticTree: Boolean,
        showMsil: Boolean,
        fileName: String
    ) {
        var prog: AstNode? = null
        try {
            prog = parse(programFile)
        } catch (e: Exception) {
            println(String.format("Ошибка: %s", e.message))
            exitProcess(1)
        }

        if (showBaseTree) {
            println("ast:")
            println(printTree(prog.tree(), System.lineSeparator()))
        }

        val checker = SemanticChecker()
        val scope: IdentScope = SemanticChecker.prepareGlobalScope()

        checker.semanticCheck(prog, scope)

        if (SemanticChecker.errors.isNotEmpty()) {
            println("Ошибки компиляции:")
            SemanticChecker.errors.forEach { println(it) }
            return
        }

        if (showSemanticTree) {
            println()
            println("semantic-check:")
            println(printTree(prog.tree(), System.lineSeparator()))
            println()
        }

        if (showMsil) {
            println()
            println("msil:")
        }
        if (showMsil) {
            try {
                val gen: MsilCodeGenerator = MsilCodeGenerator()
                gen.msilGen(prog)
                val resultMsil: String = Printer.printTree(gen.code(), System.lineSeparator())
                println(resultMsil)
//                Printer.writeToFile(fileName.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()[0] + ".msil", resultMsil, false)
            } catch (e: Exception) {
                println(String.format("Ошибка: %s", e.message))
                System.exit(3)
            }
        }
    }
}
