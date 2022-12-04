fun main() {
    class Range(val from: Int, val to: Int) {
        init {
            require(from <= to)
        }

        fun contains(other: Range): Boolean =
            from <= other.from && other.to <= to

        fun intersects(other: Range): Boolean =
            when {
                to < other.from -> false
                other.to < from -> false

                else -> true
            }
    }

    fun parseRange(range: String): Range {
        val (from, to) = range.split("-", limit = 2)

        return Range(from.toInt(), to.toInt())
    }

    fun parsePairOrRanges(pair: String): Pair<Range, Range> {
        val ranges = pair.split(",", limit = 2)
        val (left, right) = ranges.map { parseRange(it) }

        return left to right
    }

    fun part1(input: List<String>): Int {
        return input.count { pairOfRanges ->
            val (left, right) = parsePairOrRanges(pairOfRanges)

            left.contains(right) || right.contains(left)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { pairOfRanges ->
            val (left, right) = parsePairOrRanges(pairOfRanges)

            left.intersects(right)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
