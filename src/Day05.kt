import kotlin.text.StringBuilder

fun main() {
    fun parseCargoLine(line: String): List<Char?> = buildList {
        var currentIdx = 0
        while (currentIdx < line.length) {
            val cargo = line.substring(currentIdx, currentIdx + 3)
            val cargoLetter = if (cargo.isNotBlank()) {
                cargo.removeSurrounding("[", "]").single()
            } else {
                null
            }

            add(cargoLetter)

            currentIdx += 4
        }
    }

    fun parseCargoSetup(setup: List<String>): List<StringBuilder> {
        val cargoLines = setup.dropLast(1).asReversed()
        val cargoLevels = cargoLines.map { level -> parseCargoLine(level) }

        val result = MutableList(cargoLevels.first().size) { StringBuilder() }

        cargoLevels.forEach { cargoLevel ->
            cargoLevel.forEachIndexed { idx, cargo ->
                if (cargo != null) {
                    result[idx].append(cargo)
                }
            }
        }

        return result
    }

    data class MoveCommand(val from: Int, val to: Int, val count: Int)

    fun parseCommand(command: String): MoveCommand {
        val (count, from, to) = command
            .replace("move ", "")
            .replace("from ", "")
            .replace("to ", "")
            .split(" ", limit = 3)
            .map { it.toInt() }

        return MoveCommand(from - 1, to - 1, count)
    }

    fun StringBuilder.removeLast(count: Int): String {
        require(length >= count)
        val tail = takeLast(count).toString()
        setLength(length - count)
        return tail
    }

    fun parseInput(input: List<String>): Pair<List<StringBuilder>, List<MoveCommand>> {
        val separatorPosition = input.indexOfFirst { it.isBlank() }

        val crates = parseCargoSetup(input.take(separatorPosition))
        val commands = input.drop(separatorPosition + 1).map { parseCommand(it) }

        return crates to commands
    }

    fun crateMover9000(crates: List<StringBuilder>, command: MoveCommand) {
        val from = crates[command.from]
        val to = crates[command.to]
        val toMove = from.removeLast(command.count)
        to.append(toMove.reversed())
    }

    fun topLevelCrates(crates: List<StringBuilder>): String =
        crates.joinToString("") { it.last().toString() }

    fun part1(input: List<String>): String {
        val (crates, commands) = parseInput(input)

        commands.forEach { crateMover9000(crates, it) }

        return topLevelCrates(crates)
    }

    fun crateMover9001(crates: List<StringBuilder>, command: MoveCommand) {
        val from = crates[command.from]
        val to = crates[command.to]
        val toMove = from.removeLast(command.count)
        to.append(toMove)
    }
    
    fun part2(input: List<String>): String {
        val (crates, commands) = parseInput(input)

        commands.forEach { crateMover9001(crates, it) }

        return topLevelCrates(crates)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
