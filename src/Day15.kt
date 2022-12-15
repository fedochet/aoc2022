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

    fun parsePositions(input: List<String>) = input.map {
        val (posStr, beaconStr) = it.split(": ", limit = 2)

        val pos = parsePos(posStr.removePrefix("Sensor at "))
        val beacon = parsePos(beaconStr.removePrefix("closest beacon is at "))

        pos to beacon
    }

    fun part1(input: List<String>, y: Int): Int {
        val dots = parsePositions(input)

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

    fun notInRadius(dotAndBeacon: Pair<Pos, Pos>, possibleDot: Pos): Boolean {
        val (dot, beacon) = dotAndBeacon
        return distance(dot, possibleDot) > distance(dot, beacon)
    }

    fun walkAround(pos: Pos, radius: Int): Sequence<Pos> = sequence {
        var xAdd = radius
        var yAdd = 0

        while (yAdd < radius) {
            xAdd--
            yAdd++

            yield(Pos(pos.x + xAdd, pos.y + yAdd))
        }

        while (xAdd > -radius) {
            xAdd--
            yAdd--

            yield(Pos(pos.x + xAdd, pos.y + yAdd))
        }

        while (yAdd > -radius) {
            xAdd++
            yAdd--

            yield(Pos(pos.x + xAdd, pos.y + yAdd))
        }

        while (xAdd < radius) {
            xAdd++
            yAdd++

            yield(Pos(pos.x + xAdd, pos.y + yAdd))
        }
    }

    fun positionFrequency(possibleDot: Pos): Long = possibleDot.x.toLong() * 4_000_000L + possibleDot.y.toLong()

    fun part2(input: List<String>, x: Int, y: Int): Long {
        val dots = parsePositions(input)

        fun inArea(pos: Pos): Boolean = pos.x in 0..x && pos.y in 0..y

        val targetPoint =
            dots.asSequence()
                .flatMap { (dot, beacon) -> walkAround(dot, distance(dot, beacon) + 1) }
                .filter { inArea(it) }
                .first { candidate -> dots.all { pair -> notInRadius(pair, candidate) } }

        return positionFrequency(targetPoint)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, y = 10) == 26)
    check(part2(testInput, x = 20, y = 20) == 56000011L)

    val input = readInput("Day15")
    println(part1(input, y = 2_000_000))
    println(part2(input, x = 4_000_000, y = 4_000_000))
}

// 4436198
// 4535198