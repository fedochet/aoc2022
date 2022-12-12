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

    operator fun <E> List<List<E>>.get(pos: Pos): E = this[pos.row][pos.column]

    data class ParseGraphResult(
        val graph: List<List<Int>>,
        val start: Pos,
        val finish: Pos
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


    fun exploreGraph(
        graph: List<List<Int>>,
        start: Pos,
        canGo: (from: Int, to: Int) -> Boolean
    ): Sequence<PosAndDistance> = sequence {
        val rows = graph.size
        val columns = graph.first().size

        val queue = ArrayDeque(listOf(PosAndDistance(start, 0)))
        val visited = mutableSetOf<Pos>()

        while (queue.isNotEmpty()) {
            val (current, distance) = queue.removeFirst()

            if (!visited.add(current)) {
                continue
            }

            yield(PosAndDistance(current, distance))

            val currentHeight = graph[current]

            val nextPositions =
                sequenceOf(current.moveLeft(), current.moveRight(), current.moveUp(), current.moveDown())
                    .filter { it.row in 0 until rows && it.column in 0 until columns }
                    .filter { canGo(currentHeight, graph[it]) }
                    .filter { it !in visited }

            queue += nextPositions.map { PosAndDistance(it, distance + 1) }
        }
    }

    fun part1(input: List<String>): Int {
        val (graph, start, finish) = parseGraph(input)

        return exploreGraph(graph, start, canGo = { from, to -> to <= from + 1 })
            .first { it.pos == finish }
            .distance
    }

    fun part2(input: List<String>): Int {
        val (graph, _, finish) = parseGraph(input)

        return exploreGraph(graph, start = finish, canGo = { from, to -> from <= to + 1 })
            .filter { graph[it.pos] == 0 }
            .minBy { it.distance }
            .distance
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
