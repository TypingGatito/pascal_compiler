package semantic.base

import exceptions.SemanticException

class IdentScope() {
    var idents: MutableMap<String, IdentDesc> = mutableMapOf()
    var parent: IdentScope? = null
    var func: IdentDesc? = null
    var varIndex = 0
    var paramIndex = 0

    constructor(parent: IdentScope) : this() {
        this.parent = parent
    }

    fun currGlobal(): IdentScope {
        var curr = this
        while (curr.parent != null) {
            curr = curr.parent!!
        }
        return curr
    }

    fun currFunc(): IdentScope? {
        var curr: IdentScope? = this
        while (curr != null && curr.func == null) {
            curr = curr.parent
        }
        return curr
    }

    fun getIdent(name: String?): IdentDesc? {
        var scope: IdentScope? = this
        var ident: IdentDesc? = null
        while (scope != null) {
            ident = scope.idents[name]
            if (ident != null) {
                break
            }
            scope = scope.parent
        }
        return ident
    }

    @Throws(SemanticException::class)
    fun addIdent(ident: IdentDesc): IdentDesc {
        val funcScope = this.currFunc()
        val globalScope = this.currGlobal()

        if (ident.scope != ScopeType.PARAM) {
            ident.scope =
                (if (funcScope != null) ScopeType.LOCAL else (if (this == globalScope) ScopeType.GLOBAL else ScopeType.GLOBAL_LOCAL))
        }

        val oldIdent = this.getIdent(ident.name)
        if (oldIdent != null) {
            var error = false
            if (ident.scope == ScopeType.PARAM) {
                if (oldIdent.scope == ScopeType.PARAM) {
                    error = true
                }
            } else if (ident.scope == ScopeType.LOCAL) {
                if (oldIdent.scope != ScopeType.GLOBAL
                    && oldIdent.scope != ScopeType.GLOBAL_LOCAL
                ) {
                    error = true
                }
            } else {
                error = true
            }
            if (error) {
                throw SemanticException(java.lang.String.format("Идентификатор %s уже объявлен", ident.name))
            }
        }

        if (!ident.type.isFunc) {
            if (ident.scope == ScopeType.PARAM) {
                ident.index = (funcScope?.paramIndex) ?: 0
                funcScope?.paramIndex = (funcScope?.paramIndex ?: 0) + 1
            } else {
                val identScope = funcScope ?: globalScope
                ident.index = (identScope.varIndex)
                identScope.varIndex = (identScope.varIndex + 1)
            }
        }

        idents[ident.name] = ident
        return ident
    }

}