fun main() {
    class Range(val from: Int, val to: Int) {
        init {
            require(from <= to)
        }

        fun contains(other: Range): Boolean =
            from <= other.from && other.to <= to
    }

    fun parseRange(range: String): Range {
        val (from, to) = range.split("-", limit = 2)

        return Range(from.toInt(), to.toInt())
    }

    fun part1(input: List<String>): Int {
        return input.count { pairOfRanges ->
            val ranges = pairOfRanges.split(",", limit = 2)
            val (left, right) = ranges.map { parseRange(it) }

            left.contains(right) || right.contains(left)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)

    val input = readInput("Day04")
    println(part1(input))
}
