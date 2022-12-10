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

    fun part1(input: List<String>): Int {
        val initialCommands = input.map { parseCommand(it) }
        val assembly = initialCommands
            .flatMap {
                when (it) {
                    is AddX -> listOf(Noop, Add(it.value))
                    is Noop -> listOf(it)
                }
            }

        val recordedCycles =
            listOf(20, 60, 100, 140, 180, 220)
                .associateWith { 0 }
                .toMutableMap()

        var register = 1
        var cycle = 1

        for (cmd in assembly) {
            if (cycle in recordedCycles) {
                recordedCycles[cycle] = register
            }

            when (cmd) {
                is Add -> register += cmd.value
                Noop -> {}
            }

            cycle += 1
        }

        return recordedCycles.toList().sumOf { (cycles, value) -> cycles * value }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)

    val input = readInput("Day10")
    println(part1(input))
}
