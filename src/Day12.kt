fun main() {
    fun parseHeight(char: Char): Int {
        require(char in 'a'..'z')

        return char - 'a'
    }

    data class Pos(val row: Int, val column: Int) {
        fun moveLeft() = Pos(row, column - 1)
        fun moveRight() = Pos(row, column + 1)
        fun moveUp() = Pos(row + 1, column)
        fun moveDown() = Pos(row - 1, column)
    }

    data class ParseGraphResult(
        val graph: List<List<Int>>,
        val from: Pos,
        val to: Pos
    )

    fun parseGraph(input: List<String>): ParseGraphResult {
        var from: Pos? = null
        var to: Pos? = null

        val result = mutableListOf<MutableList<Int>>()

        for ((row, rowLine) in input.withIndex()) {
            val resultRow = mutableListOf<Int>()
            result.add(resultRow)

            for ((column, heightChar) in rowLine.withIndex()) {
                val height = when (heightChar) {
                    'S' -> {
                        from = Pos(row, column)

                        parseHeight('a')
                    }

                    'E' -> {
                        to = Pos(row, column)

                        parseHeight('z')
                    }

                    else -> parseHeight(heightChar)
                }

                resultRow.add(height)
            }
        }

        requireNotNull(from)
        requireNotNull(to)

        return ParseGraphResult(result, from, to)
    }

    data class PosAndDistance(val pos: Pos, val distance: Int)

    fun part1(input: List<String>): Int {
        val (graph, from, to) = parseGraph(input)

        val rows = graph.size
        val columns = graph.first().size

        val queue = ArrayDeque(listOf(PosAndDistance(from, 0)))
        val visited = mutableSetOf<Pos>()

        while (queue.isNotEmpty()) {
            val (current, distance) = queue.removeFirst()

            if (!visited.add(current)) {
                continue
            }

            if (current == to) {
                return distance
            }

            val currentHeight = graph[current.row][current.column]

            val nextPositions =
                sequenceOf(current.moveLeft(), current.moveRight(), current.moveUp(), current.moveDown())
                    .filter { it.row in 0 until rows && it.column in 0 until columns }
                    .filter { graph[it.row][it.column] <= currentHeight + 1 }
                    .filter { it !in visited }

            queue += nextPositions.map { PosAndDistance(it, distance + 1) }
        }

        error("We should have visited '$to' position")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)

    val input = readInput("Day12")
    println(part1(input))
}
