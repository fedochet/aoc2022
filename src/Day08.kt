fun main() {
    data class Pos(val row: Int, val column: Int) {
        fun moveLeft() = Pos(row, column - 1)
        fun moveRight() = Pos(row, column + 1)
        fun moveUp() = Pos(row - 1, column)
        fun moveDown() = Pos(row + 1, column)
    }

    class Forest(private val trees: List<String>) {
        val rows: Int = trees.size
        val columns: Int = trees.first().length

        operator fun get(row: Int, column: Int): Int {
            return trees[row][column].digitToInt()
        }

        operator fun get(pos: Pos): Int {
            return this[pos.row, pos.column]
        }

        operator fun contains(pos: Pos): Boolean {
            return pos.column in 0 until columns && pos.row in 0 until rows
        }
    }

    fun checkVisibility(forest: Forest, pos: Pos): Boolean {
        val positionHeight = forest[pos]

        val leftRange = generateSequence(pos) { it.moveLeft() }
        val rightRange = generateSequence(pos) { it.moveRight() }

        val upRange = generateSequence(pos) { it.moveUp() }
        val downRange = generateSequence(pos) { it.moveDown() }

        return listOf(leftRange, rightRange, upRange, downRange)
            .map { direction -> direction.drop(1).takeWhile { it in forest } }
            .any { direction ->
                direction.all { forest[it] < positionHeight }
            }
    }

    fun part1(input: List<String>): Int {
        val forest = Forest(input)

        var seen = 0
        for (row in 0 until forest.rows) {
            for (column in 0 until forest.columns) {
                if (checkVisibility(forest, Pos(row, column))) {
                    seen += 1
                }
            }
        }

        return seen
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)

    val input = readInput("Day08")
    println(part1(input))
}