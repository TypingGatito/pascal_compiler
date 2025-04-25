package semantic.base

class IdentDesc(
    var name: String,
    var type: TypeDesc,
) {
    var scope = ScopeType.GLOBAL
    var index = 0
    var builtIn = false

    constructor(name: String,
                type: TypeDesc,
                scope: ScopeType?,
                index: Int?): this(name, type) {
        this.scope = scope ?: this.scope
        this.index = index ?: this.index
    }

    override fun toString(): String {
        return java.lang.String.format(
            "%s, %s, %s",
            type.toString(), scope.toString(), if (this.builtIn) "built-in" else index.toString()
        )
    }

}