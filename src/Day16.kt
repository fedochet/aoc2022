fun main() {
    class ValueInfo(val name: String, val rate: Int, val connectedTo: List<String>)

    fun parseValve(str: String): ValueInfo {
        val pattern = Regex("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (\\w+(, \\w+)*)")
        val (name, rate, connectedTo) = pattern.matchEntire(str)?.destructured ?: error("Cannot parse $str")
        return ValueInfo(name, rate.toInt(), connectedTo.split(", "))
    }

    fun computeDistances(valves: List<ValueInfo>, nameToIndex: Map<String, Int>): Array<IntArray> {
        val distances = List(valves.size) { MutableList(valves.size) { Int.MAX_VALUE.toLong() } }

        for (i in valves.indices) {
            distances[i][i] = 0

            for (next in valves[i].connectedTo) {
                val nextIdx = nameToIndex.getValue(next)

                distances[nextIdx][i] = 1
                distances[i][nextIdx] = 1
            }
        }

        for (middle in distances.indices) {
            for (from in distances.indices) {
                for (to in distances.indices) {
                    val newLength = distances[from][middle] + distances[middle][to]

                    if (distances[from][to] > newLength) {
                        distances[from][to] = newLength
                        distances[to][from] = newLength
                    }
                }
            }
        }

        return distances.map { row -> row.map { it.toInt() }.toIntArray() }.toTypedArray()
    }

    fun findOptimalPath(
        from: Int,
        distances: Array<IntArray>,
        nameToRate: IntArray,
        toVisit: BooleanArray,
        remainingTime: Int
    ): Int {
        if (remainingTime <= 0) return 0

        val currentImpact = nameToRate[from] * remainingTime

        var nextImpact = 0
        for (next in toVisit.indices) {
            if (nameToRate[next] == 0 || !toVisit[next]) continue
            toVisit[next] = false

            val wasted = distances[from][next] + 1 // time to open the valve

            val impact = findOptimalPath(
                next,
                distances,
                nameToRate,
                toVisit,
                remainingTime - wasted
            )

            toVisit[next] = true

            if (nextImpact < impact) {
                nextImpact = impact
            }
        }

        return currentImpact + nextImpact
    }

    fun part1(input: List<String>): Int {
        val valves = input.map { parseValve(it) }

        val graph = mutableMapOf<String, Set<String>>()
        for (valve in valves) {
            graph[valve.name] = valve.connectedTo.toSet()
        }

        val nameToIndex = valves.withIndex().associate { (idx, value) -> value.name to idx }
        val distances = computeDistances(valves, nameToIndex)

        val startIdx = valves.indexOfFirst { it.name == "AA" }

        return findOptimalPath(
            startIdx,
            distances,
            valves.map { it.rate }.toIntArray(),
            valves.map { true }.toBooleanArray(),
            30
        )
    }

    fun findOptimalPathWithElephant(
        from: Int,
        startForElephant: Int,
        distances: Array<IntArray>,
        nameToRate: IntArray,
        toVisit: BooleanArray,
        remainingTime: Int
    ): Int {
        if (remainingTime <= 0) return 0

        val currentImpact = nameToRate[from] * remainingTime

        var nextImpact = 0
        for (next in toVisit.indices) {
            if (nameToRate[next] == 0 || !toVisit[next]) continue
            toVisit[next] = false

            val wasted = distances[from][next] + 1 // time to open the valve

            val impact = findOptimalPathWithElephant(
                next,
                startForElephant,
                distances,
                nameToRate,
                toVisit,
                remainingTime - wasted
            )

            toVisit[next] = true

            if (nextImpact < impact) {
                nextImpact = impact
            }
        }

        val elephantPath = findOptimalPath(startForElephant, distances, nameToRate, toVisit, 26)
        if (nextImpact < elephantPath) {
            nextImpact = elephantPath
        }

        return currentImpact + nextImpact
    }

    fun part2(input: List<String>): Int {
        val valves = input.map { parseValve(it) }

        val graph = mutableMapOf<String, Set<String>>()
        for (valve in valves) {
            graph[valve.name] = valve.connectedTo.toSet()
        }

        val nameToIndex = valves.withIndex().associate { (idx, value) -> value.name to idx }
        val distances = computeDistances(valves, nameToIndex)

        val startIdx = valves.indexOfFirst { it.name == "AA" }

        return findOptimalPathWithElephant(
            startIdx,
            startForElephant = startIdx,
            distances,
            valves.map { it.rate }.toIntArray(),
            valves.map { true }.toBooleanArray(),
            26
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
