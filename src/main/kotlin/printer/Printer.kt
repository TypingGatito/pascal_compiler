package printer

import java.io.FileReader
import java.io.FileWriter

object Printer {
    fun printTree(tree: List<String?>, sep: String?): String {
        val sb = StringBuilder()
        for (item in tree) {
            sb.append(item)
            sb.append(sep)
        }
        return sb.toString()
    }

    fun printList(list: List<Any>, sep: String?): String {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item.toString())
            sb.append(sep)
        }
        return sb.toString()
    }

    fun printList(list: ByteArray, sep: String?): String {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(">")
            sb.append(item.toInt())
            sb.append(sep)
        }
        sb.append("\n").append(String(list))
        return sb.toString()
    }

    fun readFile(file: String?): String {
        val sb = StringBuilder()
        try {
            val fr = FileReader(file)
            var c: Int
            while ((fr.read().also { c = it }) != -1) {
                sb.append(c.toChar())
            }
            fr.close()
        } catch (e: Exception) {
            println("ERROR: " + e.message)
        }
        return sb.toString()
    }

    fun writeToFile(file: String?, text: String?, append: Boolean) {
        try {
            val fw = FileWriter(file, append)
            fw.write(text)
            fw.close()
        } catch (e: Exception) {
            println("ERROR: " + e.message)
        }
    }
}
