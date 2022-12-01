fun main() {
    fun splitByEmptyLines(input: List<String>): List<List<String>> {
        val emptyLinesPositions = input.mapIndexedNotNull { index, line ->
            if (line.isBlank()) index else null
        }

        return buildList {
            var currentStart = 0

            for (idx in emptyLinesPositions) {
                add(input.subList(currentStart, idx))

                currentStart = idx + 1
            }

            add(input.subList(currentStart, input.size))
        }
    }

    /**
     * Sum of calories for elf with the biggest amount of calories.
     */
    fun part1(input: List<String>): Int {
        val subLists = splitByEmptyLines(input)

        return subLists
                .maxOfOrNull { subList -> subList.sumOf { it.toInt() } }
                ?: error("At least one elf should be present")
    }

    /**
     * Sum of calories for 3 elves with the biggest amount of calories.
     */
    fun part2(input: List<String>): Int {
        val subLists = splitByEmptyLines(input)

        return subLists
                .map { subList -> subList.sumOf { it.toInt() } }
                .sorted()
                .takeLast(3)
                .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
