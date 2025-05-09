package code_generation.base

class CodeLine(code: Any?, params: List<Any>?, label: CodeLabel?, indent: String?) {
    var code: Any?
    var params: List<Any>? = ArrayList()
    var label: CodeLabel? = null
    var indent: String? = null

    init {
        var code = code
        var label = label
        if (code is CodeLabel) {
            label = code
            code = null
        }
        this.code = code
        this.params = params
        this.label = label
        this.indent = indent
    }

    override fun toString(): String {
        val sb = StringBuilder()
        if (this.label != null) {
            if (this.indent != null) {
                sb.append(indent!!.substring(2))
            }
            sb.append(this.label).append(":")
            if (this.code != null) {
                sb.append(" ")
            }
        } else {
            if (this.indent != null) {
                sb.append(this.indent)
            }
        }
        if (this.code != null) {
            sb.append(code.toString())
            if (this.params != null) {
                for (p in params!!) {
                    sb.append(" ").append(p.toString())
                }
            }
        }
        return sb.toString()
    }
}
