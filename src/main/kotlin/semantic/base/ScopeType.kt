package semantic.base

enum class ScopeType(
    val value: String
) {
    GLOBAL("global"),
    GLOBAL_LOCAL("global.local"),
    PARAM("param"),
    LOCAL("local");

    override fun toString() = value
}