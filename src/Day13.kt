private sealed class Item : Comparable<Item> {
    override fun compareTo(other: Item): Int {
        return when {
            this is IntItem && other is IntItem -> this.value.compareTo(other.value)

            this is ListItem && other is ListItem -> {
                val nonEqualItem = this.values.zip(other.values) { l, r -> l.compareTo(r) }.firstOrNull { it != 0 }
                nonEqualItem ?: this.values.size.compareTo(other.values.size)
            }

            else -> {
                val left = wrapToListIfNeeded(this)
                val right = wrapToListIfNeeded(other)

                left.compareTo(right)
            }
        }
    }

    private fun wrapToListIfNeeded(item: Item): ListItem =
        when (item) {
            is ListItem -> item
            is IntItem -> ListItem(item)
        }
}

private data class IntItem(val value: Int) : Item()
private data class ListItem(val values: List<Item>) : Item() {
    constructor(vararg items: Item): this(items.toList())
}

private fun splitList(content: String): List<String> {
    if (content.isEmpty()) return emptyList()

    val result = mutableListOf<String>()

    var depth = 0
    var prev = 0

    for ((idx, c) in content.withIndex()) {
        when (c) {
            '[' -> depth++
            ']' -> depth--

            ',' -> if (depth == 0) {
                result += content.substring(prev, idx)
                prev = idx + 1
            }
        }
    }

    result += content.substring(prev)

    return result
}

private fun parseList(str: String): ListItem {
    val content = splitList(str.removeSurrounding("[", "]"))

    return ListItem(content.map { parseItem(it) })
}

private fun parseInt(str: String): IntItem = IntItem(str.toInt())

private fun parseItem(str: String): Item =
    if (str.startsWith("[") && str.endsWith("]")) {
        parseList(str)
    } else {
        parseInt(str)
    }

fun main() {

    fun inRightOrder(items: List<ListItem>): Boolean {
        val correctOrder = items.sorted()
        return items == correctOrder
    }

    fun parseItems(input: List<String>): List<ListItem> {
        return input.mapNotNull { if (it.isNotBlank()) parseList(it) else null }
    }

    fun part1(input: List<String>): Int {
        val items = parseItems(input)
        val pairs = items.chunked(size = 2)

        val indexesOfSorted = pairs
            .withIndex()
            .filter { inRightOrder(it.value) }

        return indexesOfSorted.sumOf { it.index + 1 }
    }

    fun part2(input: List<String>): Int {
        val items = parseItems(input)

        val separators = listOf(
            ListItem(ListItem(IntItem(2))),
            ListItem(ListItem(IntItem(6))),
        )

        val sorted = (items + separators).sorted()
        val indexesOfSeparators = separators.map { sorted.indexOf(it) }

        return indexesOfSeparators.map { it + 1}.reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}