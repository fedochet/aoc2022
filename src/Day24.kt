object Day24 {

    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        companion object {
            fun parse(char: Char): Direction = when (char) {
                '^' -> UP
                'v' -> DOWN
                '<' -> LEFT
                '>' -> RIGHT

                else -> error("Unexpected char '$char'")
            }
        }
    }

    private data class Pos(val x: Int, val y: Int) {
        fun move(direction: Direction): Pos = when (direction) {
            Direction.UP -> copy(y = y - 1)
            Direction.DOWN -> copy(y = y + 1)
            Direction.LEFT -> copy(x = x - 1)
            Direction.RIGHT -> copy(x = x + 1)
        }

        fun possibleMoves(): List<Pos> = Direction.values().map { move(it) } + this
    }

    private fun parseInitialState(input: List<String>) = input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, char ->
            when (char) {
                '#', '.' -> null

                else -> (Pos(x, y) to Direction.parse(char))
            }
        }
    }.groupBy({ it.first }, { it.second })

    private fun nextState(state: Map<Pos, List<Direction>>, xSize: Int, ySize: Int): Map<Pos, List<Direction>> {
        return state.flatMap { (pos, directions) ->
            directions.map { direction ->
                val next = pos.move(direction)

                when {
                    next.x == 0 -> next.copy(x = xSize - 2)
                    next.x == xSize - 1 -> next.copy(x = 1)

                    next.y == 0 -> next.copy(y = ySize - 2)
                    next.y == ySize - 1 -> next.copy(y = 1)

                    else -> next
                } to direction
            }
        }.groupBy({ it.first }, { it.second })
    }


    private fun measureSteps(
        start: Pos,
        initialState: Map<Pos, List<Direction>>,
        end: Pos,
        xMax: Int,
        yMax: Int
    ): Pair<Int, Map<Pos, List<Direction>>> {
        fun exists(pos: Pos): Boolean {
            return pos == start ||
                    pos == end ||
                    pos.x in 1..xMax - 2 && pos.y in 1..yMax - 2
        }

        val sequenceOfSteps = generateSequence(listOf(start) to initialState) { (cells, state) ->
            val nextState = nextState(state, xMax, yMax)
            val nextCells = cells.asSequence()
                .flatMap { it.possibleMoves() }
                .filter { exists(it) }
                .filter { it !in nextState }
                .distinct()
                .toList()

            nextCells to nextState
        }

        val (idx, cellsAndState) = sequenceOfSteps.withIndex().first { end in it.value.first }

        return idx to cellsAndState.second
    }

    fun part1(input: List<String>): Int {
        val yMax = input.size
        val xMax = input.first().length

        val start = Pos(1, 0)
        val end = Pos(xMax - 2, yMax - 1)

        val initialState = parseInitialState(input)

        return measureSteps(start, initialState, end, xMax, yMax).first
    }

    fun part2(input: List<String>): Int {
        val xMax = input.first().length
        val yMax = input.size

        val start = Pos(1, 0)
        val end = Pos(xMax - 2, yMax - 1)

        val initialState = parseInitialState(input)

        val (steps1, state1) = measureSteps(start, initialState, end, xMax, yMax)
        val (steps2, state2) = measureSteps(end, state1, start, xMax, yMax)
        val (steps3, _) = measureSteps(start, state2, end, xMax, yMax)

        return steps1 + steps2 + steps3
    }



    @JvmStatic
    fun main(args: Array<String>) {
        // test if implementation meets criteria from the description, like:
        val testInput = readInput("Day24_test")
        check(part1(testInput) == 18)
        check(part2(testInput) == 54)

        val input = readInput("Day24")
        println(part1(input))
        println(part2(input))
    }
}