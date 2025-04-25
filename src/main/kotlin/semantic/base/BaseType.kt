package semantic.base

enum class BaseType(
    val value: String
) {
    VOID("void"),
    INT("integer"),
    REAL("real"),
    BOOL("boolean"),
    STR("string"),
    CHAR("char");

    override fun toString() = value

    companion object {
        fun byValue(value: String): BaseType? {
            for (baseType in BaseType.entries) {
                if (baseType.value == value) {
                    return baseType
                }
            }
            return null
        }
    }
}