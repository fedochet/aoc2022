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

        operator fun get(pos: Pos): Int {
            return trees[pos.row][pos.column].digitToInt()
        }

        operator fun contains(pos: Pos): Boolean {
            return pos.column in 0 until columns && pos.row in 0 until rows
        }
    }

    fun goIntoForest(forest: Forest, from: Pos, action: (Pos) -> Pos): Sequence<Pos> =
        generateSequence(from, action).drop(1).takeWhile { it in forest }

    fun seenFromDirection(forest: Forest, direction: Sequence<Pos>, pos: Pos) =
        direction.all { forest[it] < forest[pos] }

    fun checkVisibility(forest: Forest, pos: Pos): Boolean {
        val leftRange = goIntoForest(forest, pos, Pos::moveLeft)
        val rightRange = goIntoForest(forest, pos, Pos::moveRight)

        val upRange = goIntoForest(forest, pos, Pos::moveUp)
        val downRange = goIntoForest(forest, pos, Pos::moveDown)

        return listOf(leftRange, rightRange, upRange, downRange)
            .any { direction -> seenFromDirection(forest, direction, pos) }
    }

    fun positionsFor(forest: Forest): Sequence<Pos> = sequence {
        for (row in 0 until forest.rows) {
            for (column in 0 until forest.columns) {
                yield(Pos(row, column))
            }
        }
    }

    fun part1(input: List<String>): Int {
        val forest = Forest(input)

        return positionsFor(forest).count { checkVisibility(forest, it) }
    }

    fun countVisibleTrees(forest: Forest, direction: Sequence<Pos>, viewPos: Pos): Int {
        var count = 0
        
        for (pos in direction) {
            count += 1

            if (forest[pos] >= forest[viewPos]) break
        }

        return count
    }

    fun ratePosition(forest: Forest, pos: Pos): Int {
        val leftRange = goIntoForest(forest, pos, Pos::moveLeft)
        val rightRange = goIntoForest(forest, pos, Pos::moveRight)

        val upRange = goIntoForest(forest, pos, Pos::moveUp)
        val downRange = goIntoForest(forest, pos, Pos::moveDown)

        val numberOfTreesFromSides = listOf(leftRange, rightRange, upRange, downRange)
            .map { direction -> countVisibleTrees(forest, direction, pos) }
        
        return numberOfTreesFromSides.fold(1, Int::times)
    }

    fun part2(input: List<String>): Int {
        val forest = Forest(input)

        return positionsFor(forest).maxOf { ratePosition(forest, it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}