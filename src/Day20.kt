fun main() {
    fun part1(input: List<String>): Int {
        val values = input.map { it.toInt() }
        val indexed = values.mapIndexed { idx, value -> idx to value }

        val result = indexed.toMutableList()
        for (toMove in indexed) {
            val oldIndex = result.indexOf(toMove)
            val steps = toMove.second

            val newIndex = ((oldIndex + steps) % result.lastIndex + result.lastIndex) % (result.lastIndex)

            result.removeAt(oldIndex)
            result.add(newIndex, toMove)
        }

        val resultingList = result.map { it.second }
        val zeroIndex = resultingList.indexOf(0)

        var sum = 0
        for (idx in listOf(1, 2, 3).map { it * 1000 }) {
            sum += resultingList[((idx % result.size) + zeroIndex) % result.size]
        }

        return sum
    }

    fun part2(input: List<String>): Long {
        val descriptionKey = 811589153

        val values = input.map { it.toLong() }.map { it * descriptionKey }
        val indexed = values.mapIndexed { idx, value -> idx to value }

        val result = indexed.toMutableList()
        repeat(10) {
            for (toMove in indexed) {
                val oldIndex = result.indexOf(toMove)
                val steps = toMove.second

                val newIndex = ((oldIndex + steps) % result.lastIndex + result.lastIndex) % (result.lastIndex)

                result.removeAt(oldIndex)
                result.add(newIndex.toInt(), toMove)
            }
        }

        val resultingList = result.map { it.second }
        val zeroIndex = resultingList.indexOf(0)

        var sum = 0L
        for (idx in listOf(1L, 2L, 3L).map { it * 1000 }) {
            val correctedIdx = ((idx % result.size) + zeroIndex) % result.size
            sum += resultingList[correctedIdx.toInt()]
        }

        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}