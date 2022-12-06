fun main() {
    fun consistsOfUniqueChars(s: String) = s.toSet().size == s.length

    fun part1(input: List<String>): Int {
        val message = input.single()

        return 4 + message.windowedSequence(4)
            .indexOfFirst { consistsOfUniqueChars(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)

    val input = readInput("Day06")
    println(part1(input))
}
