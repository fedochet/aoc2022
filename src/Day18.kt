fun main() {
    data class Dot(val x: Int, val y: Int, val z: Int) {
        fun move(dx: Int = 0, dy: Int = 0, dz: Int = 0): Dot = Dot(x + dx, y + dy, z + dz)
    }

    fun getDotsAround(dot: Dot): Sequence<Dot> = sequence {
        yield(dot.move(dx = 1))
        yield(dot.move(dy = 1))
        yield(dot.move(dz = 1))
        yield(dot.move(dx = -1))
        yield(dot.move(dy = -1))
        yield(dot.move(dz = -1))
    }

    fun parseDots(input: List<String>) = input.map { line ->
        val (x, y, z) = line.split(",", limit = 3).map { it.toInt() }

        Dot(x, y, z)
    }

    fun part1(input: List<String>): Int {
        val dots = parseDots(input)

        val pattern = mutableSetOf<Dot>()
        var sides = 0

        for (dot in dots) {
            require(pattern.add(dot))

            val dotsAround = getDotsAround(dot)

            val sidesDiff = dotsAround.map { if (it in pattern) -1 else 1 }.sum()
            sides += sidesDiff
        }

        return sides
    }

    fun part2(input: List<String>): Int {
        val dots = parseDots(input)

        val xMin = dots.minOf { it.x } - 1
        val yMin = dots.minOf { it.y } - 1
        val zMin = dots.minOf { it.z } - 1

        val xMax = dots.maxOf { it.x } + 1
        val yMax = dots.maxOf { it.y } + 1
        val zMax = dots.maxOf { it.z } + 1

        fun inBox(dot: Dot): Boolean {
            val (x, y, z) = dot

            return x in xMin..xMax &&
                    y in yMin..yMax &&
                    z in zMin..zMax
        }

        val outside = mutableSetOf<Dot>()
        val start = Dot(xMin, yMin, zMin)
        val queue = ArrayDeque(listOf(start))

        while (queue.isNotEmpty()) {
            val dot = queue.removeFirst()

            if (!outside.add(dot)) continue
            val dotsAround = getDotsAround(dot)

            val newOutside = dotsAround.filter { inBox(it) && it !in dots && it !in outside }
            queue.addAll(newOutside)
        }

        var areaOutside = 0
        for (dot in dots) {
            val dotsAround = getDotsAround(dot)

            for (around in dotsAround) {
                if (around in outside) {
                    areaOutside += 1
                }
            }
        }

        return areaOutside
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}