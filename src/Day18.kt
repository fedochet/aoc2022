fun main() {
    data class Dot(val x: Int, val y: Int, val z: Int) {
        fun move(dx: Int = 0, dy: Int = 0, dz: Int = 0): Dot = Dot(x + dx, y + dy, z + dz)
    }

    fun part1(input: List<String>): Int {
        val dots = input.map { line ->
            val (x, y, z) = line.split(",", limit = 3).map { it.toInt() }

            Dot(x, y, z)
        }

        val pattern = mutableSetOf<Dot>()
        var sides = 0

        for (dot in dots) {
            require(pattern.add(dot))

            val dotsAround = listOf(
                dot.move(dx = 1),
                dot.move(dy = 1),
                dot.move(dz = 1),
                dot.move(dx = -1),
                dot.move(dy = -1),
                dot.move(dz = -1),
            )

            val sidesDiff = dotsAround.map { if (it in pattern) -1 else 1 }.sum()
            sides += sidesDiff
        }

        return sides
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)

    val input = readInput("Day18")
    println(part1(input))
}