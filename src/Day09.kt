import kotlin.math.abs
import kotlin.math.sign

private enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun main() {
    data class Move(val direction: Direction, val steps: Int)

    data class SnakeCell(private var row: Int, private var column: Int) {
        fun move(direction: Direction) {
            when (direction) {
                Direction.UP -> row--
                Direction.DOWN -> row++
                Direction.LEFT -> column--
                Direction.RIGHT -> column++
            }
        }

        fun moveTo(other: SnakeCell) {
            if (abs(row - other.row) <= 1 && abs(column - other.column) <= 1) return

            row += (other.row - row).sign
            column += (other.column - column).sign
        }
    }

    class SnakeGame(cells: Int) {
        private val snake = List(cells) { SnakeCell(0, 0) }
        private val tailPath = mutableSetOf<SnakeCell>()

        fun move(direction: Direction) {
            snake.first().move(direction)

            for ((curr, next) in snake.zipWithNext()) {
                next.moveTo(curr)
            }

            tailPath.add(snake.last().copy())
        }

        val visitedCells: Int
            get() = tailPath.size
    }

    fun parseMove(move: String): Move {
        val (directionStr, stepsStr) = move.split(" ", limit = 2)

        val direction = when (directionStr) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "L" -> Direction.LEFT
            "R" -> Direction.RIGHT

            else -> error("Unexpected direction '$directionStr'")
        }

        return Move(direction, stepsStr.toInt())
    }

    fun part1(input: List<String>): Int {
        val snakeGame = SnakeGame(cells = 2)

        val moves = input.map { parseMove(it) }
        for (move in moves) {
            repeat(move.steps) {
                snakeGame.move(move.direction)
            }
        }

        return snakeGame.visitedCells
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)

    val input = readInput("Day09")
    println(part1(input))
}
