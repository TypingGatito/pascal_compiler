package code_generation.base

import java.util.*

abstract class CodeGenerator {
    private var codeLines: MutableList<CodeLine> = ArrayList()
    var indent: String = ""

    fun add(code: String?) {
        add(code, null, null)
    }

    fun add(code: CodeLabel?) {
        add(code, null, null)
    }

    fun add(code: Any?, param: Any) {
        add(code, ArrayList(Arrays.asList(param)), null)
    }

    @JvmOverloads
    fun add(code: Any?, params: List<Any>?, label: CodeLabel? = null) {
        var code = code
        var label = label
        if (code is CodeLabel) {
            label = code
            code = null
        }
        if (code != null && (code as String).length > 0 && code[code.length - 1] == '}') {
            this.indent = indent.substring(2)
        }
        codeLines.add(CodeLine(code, params, label, this.indent))
        if (code != null && (code as String).length > 0 && code[code.length - 1] == '{') {
            this.indent = this.indent + "  "
        }
    }

    fun code(): List<String> {
        var index = 0
        for (cl in this.codeLines) {
            cl.label?.let {
                it.index = index
                index++
            }
        }
        val code: MutableList<String> = ArrayList()
        for (cl in this.codeLines) {
            code.add(cl.toString())
        }
        return code
    }

    fun getCodeLines(): List<CodeLine> {
        return codeLines
    }

    fun setCodeLines(codeLines: MutableList<CodeLine>) {
        this.codeLines = codeLines
    }
}
