import java.util.Set.of as setOf

object Day23 {
    private data class Pos(val x: Int, val y: Int) {
        fun move(direction: Direction): Pos {
            return when (direction) {
                Direction.SOUTH -> Pos(x, y + 1)
                Direction.WEST -> Pos(x - 1, y)
                Direction.NORTH -> Pos(x, y - 1)
                Direction.EAST -> Pos(x + 1, y)
            }
        }

        fun move(first: Direction, second: Direction): Pos = move(first).move(second)

        fun lookToSide(direction: Direction): Set<Pos> {
            return when (direction) {
                Direction.SOUTH -> setOf(move(Direction.SOUTH, Direction.EAST), move(Direction.SOUTH), move(Direction.SOUTH, Direction.WEST))
                Direction.WEST -> setOf(move(Direction.WEST, Direction.SOUTH), move(Direction.WEST), move(Direction.WEST, Direction.NORTH))
                Direction.NORTH -> setOf(move(Direction.NORTH, Direction.WEST), move(Direction.NORTH), move(Direction.NORTH, Direction.EAST))
                Direction.EAST -> setOf(move(Direction.EAST, Direction.NORTH), move(Direction.EAST), move(Direction.EAST, Direction.SOUTH))
            }
        }

        fun lookAround(): Set<Pos> = setOf(
            move(Direction.SOUTH),
            move(Direction.SOUTH, Direction.WEST),
            move(Direction.WEST),
            move(Direction.WEST, Direction.NORTH),
            move(Direction.NORTH),
            move(Direction.NORTH, Direction.EAST),
            move(Direction.EAST),
            move(Direction.EAST, Direction.SOUTH)
        )
    }

    private enum class Direction {
        SOUTH, WEST, NORTH, EAST
    }

    private fun chooseStep(pos: Pos, setup: Set<Pos>, lookupOrder: List<Direction>): Pos? {
        if (pos.lookAround().none { it in setup }) return null

        val next = lookupOrder.firstOrNull { direction ->
            pos.lookToSide(direction).none { it in setup }
        }

        return next?.let { pos.move(it) }
    }

    private fun performStep(current: Set<Pos>, lookupOrder: List<Direction>): Set<Pos> {
        val nextSteps = current.associateWith { (chooseStep(it, current, lookupOrder) ?: it) }
        val nextStepsCounts = nextSteps.values.groupingBy { it }.eachCount()

        return current.map { pos ->
            val nextStep = nextSteps.getValue(pos)
            if (nextStepsCounts.getValue(nextStep) == 1) nextStep else pos
        }.toSet()
    }

    private fun countUncoveredArea(setup: Set<Pos>): Int {
        val minX = setup.minOf { it.x }
        val maxX = setup.maxOf { it.x }

        val minY = setup.minOf { it.y }
        val maxY = setup.maxOf { it.y }

        val xRange = minX..maxX
        val yRange = minY..maxY

        return xRange.flatMap { x ->
            yRange.map { y ->
                Pos(x, y)
            }
        }.count { it !in setup }
    }

    private fun parsePositions(input: List<String>): List<Pos> {
        val positions = input.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                when (c) {
                    '#' -> Pos(x, y)
                    '.' -> null
                    else -> error("Unknown tile: $c")
                }
            }
        }
        return positions
    }

    private fun part1(input: List<String>): Int {
        val positions = parsePositions(input)

        val lookups = ArrayDeque(listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST))
        var current = positions.toSet()

        repeat(10) {
            current = performStep(current, lookups)
            lookups.addLast(lookups.removeFirst())
        }

        return countUncoveredArea(current)
    }

    fun part2(input: List<String>): Int {
        val positions = parsePositions(input)

        val lookups = ArrayDeque(listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST))
        var current = positions.toSet()

        var count = 0
        while (true) {
            count++

            val newState = performStep(current, lookups)
            lookups.addLast(lookups.removeFirst())

            if (newState == current) break
            current = newState
        }

        return count
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // test if implementation meets criteria from the description, like:
        val testInput = readInput("Day23_test")
        check(part1(testInput) == 110)
        check(part2(testInput) == 20)

        val input = readInput("Day23")
        println(part1(input))
        println(part2(input))
    }
}
