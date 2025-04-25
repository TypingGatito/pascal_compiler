
import Program
import org.apache.commons.cli.*
import printer.Printer.readFile
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val options = Options()

    // параметры вызова функции
    options.addOption(getOption("s", "source", true, "source code file", true))

    val parser: CommandLineParser = DefaultParser()
    val formatter = HelpFormatter()
    val cmd: CommandLine?

    try {
        cmd = parser.parse(options, args)
    } catch (e: ParseException) {
        println(e.message)
        formatter.printHelp("utility-name", options)
        exitProcess(1)
    }

    val src = cmd!!.getOptionValue("source")
    val srcText = readFile(src)

    Program.execute(srcText, src)
}

private fun getOption(
    option: String, longOption: String, hasArg: Boolean,
    description: String, required: Boolean
): Option {
    val o = Option(option, longOption, hasArg, description)
    o.isRequired = required
    return o
}