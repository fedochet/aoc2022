fun main() {
    class ValueInfo(val name: String, val rate: Int, val connectedTo: List<String>)

    fun parseValve(str: String): ValueInfo {
        val pattern = Regex("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (\\w+(, \\w+)*)")
        val (name, rate, connectedTo) = pattern.matchEntire(str)?.destructured ?: error("Cannot parse $str")
        return ValueInfo(name, rate.toInt(), connectedTo.split(", "))
    }

    fun computeDistances(valves: List<ValueInfo>, nameToIndex: Map<String, Int>): List<List<Int>> {
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

        return distances.map { row -> row.map { it.toInt() } }
    }

    fun findOptimalPath(
        from: String,
        distances: List<List<Int>>,
        nameToIndex: Map<String, Int>,
        nameToRate: Map<String, Int>,
        notVisited: Set<String>,
        remainingTime: Int
    ): Int {
        if (remainingTime <= 0) return 0

        val currentImpact = nameToRate.getValue(from) * remainingTime

        var nextImpact = 0
        for (to in notVisited) {
            val wasted = distances[nameToIndex.getValue(from)][nameToIndex.getValue(to)] + 1 // time to open the valve

            val impact = findOptimalPath(
                to,
                distances,
                nameToIndex,
                nameToRate,
                notVisited - to,
                remainingTime - wasted
            )

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

        val start = "AA"

        return findOptimalPath(
            start,
            distances,
            nameToIndex,
            valves.associate { it.name to it.rate },
            valves.filter { it.rate > 0 }.map { it.name }.toSet(),
            30
        )
    }

    fun findOptimalPathWithElephant(
        from: String,
        distances: List<List<Int>>,
        nameToIndex: Map<String, Int>,
        nameToRate: Map<String, Int>,
        notVisited: Set<String>,
        remainingTime: Int
    ): Int {
        if (remainingTime <= 0) return 0

        val currentImpact = nameToRate.getValue(from) * remainingTime

        var nextImpact = 0
        for (to in notVisited) {
            val wasted = distances[nameToIndex.getValue(from)][nameToIndex.getValue(to)] + 1 // time to open the valve

            val impact = findOptimalPathWithElephant(
                to,
                distances,
                nameToIndex,
                nameToRate,
                notVisited - to,
                remainingTime - wasted
            )

            if (nextImpact < impact) {
                nextImpact = impact
            }
        }

        val elephantPath = findOptimalPath("AA", distances, nameToIndex, nameToRate, notVisited, 26)
        if (elephantPath > nextImpact) {
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

        val start = "AA"

        return findOptimalPathWithElephant(
            start,
            distances,
            nameToIndex,
            valves.associate { it.name to it.rate },
            valves.filter { it.rate > 0 }.map { it.name }.toSet(),
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
