package code_generation.base

class CodeLabel(
    var index: Int? = null,
    var prefix: String = "L"
) {
    override fun toString(): String {
        return String.format("%s_%d", this.prefix, this.index)
    }
}
