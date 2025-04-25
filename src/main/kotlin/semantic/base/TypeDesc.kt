package semantic.base

import exceptions.SemanticException

class TypeDesc(
    val baseType: BaseType?,
) {
    var returnType: TypeDesc? = null
    var params: List<TypeDesc?>? = null

    constructor(baseType: BaseType?, returnType: TypeDesc?) : this(baseType) {
        this.returnType = returnType
    }

    constructor(baseType: BaseType?, returnType: TypeDesc?, params: List<TypeDesc?>?) : this(baseType, returnType) {
        this.params = params
    }

    val isFunc: Boolean
        get() = returnType != null

    val isSimple: Boolean
        get() = !isFunc

    override fun toString(): String {
        if (!this.isFunc) {
            return baseType.toString()
        } else {
            val sb = StringBuilder(returnType.toString())
            sb.append(" (")
            for (param in params!!) {
                if (sb[sb.length - 1] != '(') {
                    sb.append(", ")
                }
                sb.append(param.toString())
            }
            sb.append(')')
            return sb.toString()
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val other = o as TypeDesc
        if (this.isFunc != other.isFunc) {
            return false
        }
        if (!this.isFunc) {
            return baseType!!.equals(other.baseType)
        } else {
            if (this.returnType != other.returnType) {
                return false
            }
            if (params!!.size != other.params!!.size) {
                return false
            }
            for (i in params!!.indices) {
                if (params!![i] != other.params!![i]) {
                    return false
                }
            }
            return true
        }
    }

    companion object {
        val VOID: TypeDesc = TypeDesc(BaseType.VOID)

        val INT: TypeDesc = TypeDesc(BaseType.INT)

        val FLOAT: TypeDesc = TypeDesc(BaseType.REAL)

        val BOOL: TypeDesc = TypeDesc(BaseType.BOOL)

        val STR: TypeDesc = TypeDesc(BaseType.STR)

        @Throws(Exception::class)
        fun fromStr(strDec: String): TypeDesc {
            try {
                val baseType = BaseType.byValue(strDec)
                return TypeDesc(baseType!!)
            } catch (e: Exception) {
                throw SemanticException("ERROR unknown type $strDec")
            }
        }

        fun fromBaseType(baseType: BaseType): TypeDesc {
            return TypeDesc(baseType)
        }

    }
}