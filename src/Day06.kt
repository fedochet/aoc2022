fun main() {
    fun isMarker(s: String): Boolean {
        val alreadySeen = mutableSetOf<Char>()

        return s.all {
            alreadySeen.add(it)
        }
    }

    fun findPositionAfterMarker(message: String, markerLength: Int): Int {
        val startOfMarker = message.windowedSequence(size = markerLength).indexOfFirst { isMarker(it) }
        return markerLength + startOfMarker
    }

    fun part1(input: List<String>): Int {
        val message = input.single()

        return findPositionAfterMarker(message, 4)
    }

    fun part2(input: List<String>): Int {
        val message = input.single()

        return findPositionAfterMarker(message, 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
