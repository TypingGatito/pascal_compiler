package exceptions

class SemanticException : Exception {
    constructor(message: String?) : super(message)

    constructor(message: String, row: Int?, col: Int?) : super(getMessage(message, row, col))

    companion object {
        private fun getMessage(message: String, row: Int?, col: Int?): String {
            var message = message
            if (row != null || col != null) {
                val sb = StringBuilder()
                sb.append(" (")
                if (row != null) {
                    sb.append(String.format("строка: %d", row))
                    if (col != null) {
                        sb.append(", ")
                    }
                }
                if (col != null) {
                    sb.append(String.format("позиция: %d", col))
                }
                message += sb.toString()
            }
            return message
        }
    }
}
