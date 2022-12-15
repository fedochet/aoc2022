import kotlin.math.abs

fun main() {

    data class Pos(val x: Int, val y: Int)

    fun distance(from: Pos, to: Pos): Int =
        abs(from.x - to.x) + abs(from.y - to.y)

    fun parsePos(posStr: String): Pos {
        val (x, y) = posStr.split(", ", limit = 2)

        return Pos(
            x.removePrefix("x=").toInt(),
            y.removePrefix("y=").toInt()
        )
    }

    fun part1(input: List<String>, y: Int): Int {
        val dots = input.map {
            val (posStr, beaconStr) = it.split(": ", limit = 2)

            val pos = parsePos(posStr.removePrefix("Sensor at "))
            val beacon = parsePos(beaconStr.removePrefix("closest beacon is at "))

            pos to beacon
        }

        val xStart = dots.minOf { (dot, beacon) -> dot.x - distance(dot, beacon) - 1 }
        val xEnd = dots.maxOf { (dot, beacon) -> dot.x + distance(dot, beacon) + 1 }

        var count = 0

        for (x in xStart..xEnd) {
            val currentPos = Pos(x, y)

            if (dots.any { (dot, beacon) -> currentPos == dot || currentPos == beacon }) {
                continue
            }

            for ((dot, beacon) in dots) {
                if (dot != beacon && distance(dot, currentPos) <= distance(dot, beacon)) {
                    count++
                    break
                }
            }
        }

        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, y = 10) == 26)

    val input = readInput("Day15")
    println(part1(input, y = 2_000_000))
}

// 4436198
// 4535198