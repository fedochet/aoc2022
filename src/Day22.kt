object Day22 {

    private enum class MoveDirection {
        RIGHT, DOWN, LEFT, UP;

        val opposite: MoveDirection
            get() = when (this) {
                UP -> DOWN
                RIGHT -> LEFT
                DOWN -> UP
                LEFT -> RIGHT
            }

        fun turn(where: Turn): MoveDirection = when (where) {
            Turn.LEFT -> when (this) {
                UP -> LEFT
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
            }

            Turn.RIGHT -> when (this) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
            }
        }
    }

    private sealed interface Command

    private data class Move(val distance: Int) : Command

    private enum class Turn : Command {
        LEFT, RIGHT
    }

    private enum class Tile {
        EMPTY, WALL, FLOOR
    }

    private fun Char.toTile(): Tile = when (this) {
        '#' -> Tile.WALL
        '.' -> Tile.FLOOR
        ' ' -> Tile.EMPTY

        else -> throw IllegalArgumentException("Unknown tile: $this")
    }

    private fun splitToDitigsAndLetters(input: String): List<String> {
        val result = mutableListOf<String>()
        var current = ""
        for (c in input) {
            if (c.isDigit()) {
                current += c
            } else {
                if (current.isNotEmpty()) {
                    result.add(current)
                    current = ""
                }
                result.add(c.toString())
            }
        }
        if (current.isNotEmpty()) {
            result.add(current)
        }
        return result
    }

    // parses string like "10R23L5" to listOf(Move(10), Turn.RIGHT, Move(23), Turn.LEFT, Move(5))
    private fun parsePath(input: String): List<Command> {
        val lettersAndDigits = splitToDitigsAndLetters(input)

        return lettersAndDigits.map {
            if (it.first().isDigit()) {
                Move(it.toInt())
            } else {
                when (it) {
                    "R" -> Turn.RIGHT
                    "L" -> Turn.LEFT
                    else -> throw IllegalArgumentException("Unknown command: $it")
                }
            }
        }
    }

    private data class Pos(val x: Int, val y: Int) {
        fun move(direction: MoveDirection): Pos = when (direction) {
            MoveDirection.UP -> copy(x = x - 1)
            MoveDirection.RIGHT -> copy(y = y + 1)
            MoveDirection.DOWN -> copy(x = x + 1)
            MoveDirection.LEFT -> copy(y = y - 1)
        }
    }

    private fun computePassword(pos: Pos, direction: MoveDirection): Int {
        return (pos.x + 1) * 1000 + (pos.y + 1) * 4 + direction.ordinal
    }

    private fun part1(input: List<String>): Int {
        val path = parsePath(input.last())
        val labirinth = input.dropLast(2).map { it.map { it.toTile() }.toMutableList() }.toList()

        val height = labirinth.size
        val width = labirinth.maxOf { it.size }

        labirinth.forEach { row -> repeat(width - row.size) { row.add(Tile.EMPTY) } }

        var pos = Pos(0, labirinth.first().indexOf(Tile.FLOOR))
        var direction = MoveDirection.RIGHT

        fun inLabyrinth(pos: Pos): Boolean = pos.x in 0 until height && pos.y in 0 until width

        fun findFurthestTile(pos: Pos, direction: MoveDirection): Pos {
            return generateSequence(pos) { it.move(direction) }
                .takeWhile { inLabyrinth(it) }
                .filter {
                    require(inLabyrinth(it))
                    labirinth[it.x][it.y] != Tile.EMPTY
                }
                .last()
        }

        fun findNextPos(pos: Pos, direction: MoveDirection): Pos {
            val nextPos = pos.move(direction).takeIf { inLabyrinth(it) && labirinth[it.x][it.y] != Tile.EMPTY }
                ?: findFurthestTile(pos, direction.opposite)

            require(inLabyrinth(nextPos))

            return when (labirinth[nextPos.x][nextPos.y]) {
                Tile.EMPTY -> error("Unexpected wrapped tile")
                Tile.WALL -> pos
                Tile.FLOOR -> nextPos
            }
        }

        for (command in path) {
            when (command) {
                is Move -> {
                    repeat(command.distance) {
                        pos = findNextPos(pos, direction)
                    }
                }
                is Turn -> {
                    direction = direction.turn(command)
                }
            }
        }

        return computePassword(pos, direction)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // test if implementation meets criteria from the description, like:
        val testInput = readInput("Day22_test")
        check(part1(testInput) == 6032)

        val input = readInput("Day22")
        println(part1(input))
    }
}
