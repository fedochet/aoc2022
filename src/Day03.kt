fun main() {
    fun itemPriority(itemType: Char): Int = when (itemType) {
        in 'a'..'z' -> (itemType - 'a') + 1
        in 'A'..'Z' -> (itemType - 'A') + 27

        else -> error("Unexpected item type '$itemType'")
    }

    fun findCommonItem(left: String, right: String): Char {
        val commonChars = left.toSet() intersect right.toSet()

        return commonChars.singleOrNull()
            ?: error("There are multiple common char between $left and $right")
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { rucksack ->
            require(rucksack.length % 2 == 0)

            val left = rucksack.substring(0, rucksack.length / 2)
            val right = rucksack.substring(rucksack.length / 2)

            val commonItem = findCommonItem(left, right)

            itemPriority(commonItem)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println(part1(input))
}
