fun main() {
    fun itemPriority(itemType: Char): Int = when (itemType) {
        in 'a'..'z' -> (itemType - 'a') + 1
        in 'A'..'Z' -> (itemType - 'A') + 27

        else -> error("Unexpected item type '$itemType'")
    }

    fun findCommonItems(left: String, right: String): String {
        val commonItems = left.toSet() intersect right.toSet()

        return String(commonItems.toCharArray())
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { rucksack ->
            require(rucksack.length % 2 == 0)

            val left = rucksack.substring(0, rucksack.length / 2)
            val right = rucksack.substring(rucksack.length / 2)

            val commonItem = findCommonItems(left, right).singleOrNull()
                ?: error("There are multiple common items between $left and $right")

            itemPriority(commonItem)
        }
    }

    fun part2(input: List<String>): Int {
        require(input.size % 3 == 0)

        return input
            .windowed(size = 3, step = 3)
            .sumOf { group ->
                val (one, two, three) = group
                val commonItems = findCommonItems(findCommonItems(one, two), three)

                val commonItem = commonItems.singleOrNull()
                    ?: error("There are multiple common items in the group: $group")

                itemPriority(commonItem)
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
