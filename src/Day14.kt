import kotlin.math.sign

fun main() {
    data class Pos(val row: Int, val column: Int) {
        fun moveLeft() = Pos(row, column - 1)
        fun moveRight() = Pos(row, column + 1)
        fun moveDown() = Pos(row + 1, column)
    }

    fun parseWall(wallLine: String): List<Pos> =
        wallLine
            .split(" -> ")
            .map { dot ->
                val (column, row) = dot.split(",", limit = 2).map { it.toInt() }
                Pos(row, column)
            }

    fun linePositions(from: Pos, to: Pos): Sequence<Pos> = sequence {
        require(from.row == to.row || from.column == to.column)
        var current = from
        while (current != to) {
            yield(current)

            val rowStep = (to.row - from.row).sign
            val columnStep = (to.column - from.column).sign

            current = Pos(current.row + rowStep, current.column + columnStep)
        }

        yield(to)
    }
    
    fun possibleMoves(pos: Pos): List<Pos> {
        val down = pos.moveDown()
        
        return listOf(down, down.moveLeft(), down.moveRight())
    }

    fun buildInitialSetup(walls: List<List<Pos>>): Set<Pos> {
        return walls.asSequence()
            .flatMap { wall ->
                wall.asSequence().zipWithNext { from, to -> linePositions(from, to) }.flatten()
            }
            .toSet()
    }

    fun part1(input: List<String>): Int {
        val walls = input.map { parseWall(it) }

        val setup = buildInitialSetup(walls).toMutableSet()

        val lowestPoint = setup.maxOf { it.row }
        var sandUnits = 0

        outer@while (true) {
            var current = Pos(0, 500)

            while (true) {
                if (current.row > lowestPoint) {
                    break@outer
                }

                val next = possibleMoves(current).firstOrNull { it !in setup }
                if (next != null) {
                    current = next
                } else {
                    setup.add(current)
                    sandUnits++
                    
                    break
                }
            }
        }

        return sandUnits
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)

    val input = readInput("Day14")
    println(part1(input))
}
