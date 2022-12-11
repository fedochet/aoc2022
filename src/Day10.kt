private sealed interface Command

private sealed interface Assembly

private object Noop : Command, Assembly

private class AddX(val value: Int) : Command

private class Add(val value: Int) : Assembly

fun main() {
    fun parseCommand(str: String): Command {
        return when {
            str == "noop" -> Noop
            str.startsWith("addx ") -> AddX(str.removePrefix("addx ").toInt())

            else -> error("Unexpected command '$str'")
        }
    }

    fun parseAndConvertToAssembly(input: List<String>): List<Assembly> {
        val initialCommands = input.map { parseCommand(it) }
        val assembly = initialCommands
            .flatMap {
                when (it) {
                    is AddX -> listOf(Noop, Add(it.value))
                    is Noop -> listOf(it)
                }
            }

        return assembly
    }

    class Executor(val assembly: List<Assembly>) {
        var register = 1
            private set

        var cycle = 1
            private set

        fun nextStep() {
            require(hasNext)

            when (val cmd = assembly[cycle - 1]) {
                is Add -> register += cmd.value
                Noop -> {}
            }

            cycle += 1
        }

        val hasNext: Boolean
            get() = cycle < assembly.size
    }


    fun part1(input: List<String>): Int {
        val assembly = parseAndConvertToAssembly(input)

        val recordedCycles =
            listOf(20, 60, 100, 140, 180, 220)
                .associateWith { 0 }
                .toMutableMap()

        val executor = Executor(assembly)

        while (executor.hasNext) {
            if (executor.cycle in recordedCycles) {
                recordedCycles[executor.cycle] = executor.register
            }

            executor.nextStep()
        }

        return recordedCycles.toList().sumOf { (cycles, value) -> cycles * value }
    }

    fun part2(input: List<String>): String {
        val assembly = parseAndConvertToAssembly(input)
        val executor = Executor(assembly)

        val width = 40
        val height = 6

        return (0 until height).joinToString(separator = System.lineSeparator()) { _ ->
            (0 until width).joinToString("") { widthPos ->
                val sprite = executor.register

                val pixelColor: String =
                    if (widthPos in (sprite - 1)..(sprite + 1)) {
                        "#"
                    } else {
                        "."
                    }

                executor.nextStep()

                pixelColor
            }
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    val part2 = part2(testInput)
    check(part2 == """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent()
    )

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

