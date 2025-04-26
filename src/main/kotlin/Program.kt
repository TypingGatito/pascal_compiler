import ast.AstNode
import parser.Parser.parse
import printer.Printer.printTree
import semantic.base.IdentScope
import semantic.checker.SemanticChecker
import kotlin.system.exitProcess

object Program {

    fun execute(
        programFile: String,
        showBaseTree: Boolean,
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

        println()
        println("semantic-check:")
        val checker = SemanticChecker()
        val scope: IdentScope = SemanticChecker.prepareGlobalScope()

        checker.semanticCheck(prog, scope)

        if (SemanticChecker.errors.isNotEmpty()) {
            println("Ошибки компиляции:")
            SemanticChecker.errors.forEach { println(it) }
            return
        }

        println(printTree(prog.tree(), System.lineSeparator()))
        println()


//        if (!(msilOnly || jbcOnly)) {
//            println()
//            println("semantic-check:")
//        }
//        try {
//            val checker: SemanticChecker = SemanticChecker()
//            val scope: IdentScope = SemanticChecker.prepareGlobalScope()
//            checker.semanticCheck(prog, scope)
//            if (!(msilOnly || jbcOnly)) {
//                System.out.println(Printer.printTree(prog.tree(), System.lineSeparator()))
//                println()
//            }
//        } catch (e: Exception) {
//            println(String.format("Ошибка: %s", e.message))
//            System.exit(2)
//        }
//
//        if (!(msilOnly || jbcOnly)) {
//            println()
//            println("msil:")
//        }
//        if (!jbcOnly) {
//            try {
//                val gen: MsilCodeGenerator = MsilCodeGenerator()
//                gen.genProgram(prog)
//                val resultMsil: String = Printer.printTree(gen.code(), System.lineSeparator())
//                println(resultMsil)
//                Printer.writeToFile(fileName.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()[0] + ".msil", resultMsil, false)
//            } catch (e: Exception) {
//                println(String.format("Ошибка: %s", e.message))
//                System.exit(3)
//            }
//        }

    }
}
