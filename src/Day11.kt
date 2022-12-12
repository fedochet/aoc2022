fun main() {
    class Monkey(
        startingItems: List<Long>,
        private val operation: (Long) -> Long,
        val divisibleBy: Long,
    ) {
        private val queue: ArrayDeque<Long> = ArrayDeque(startingItems)

        private lateinit var nextMonkeyTrue: Monkey
        private lateinit var nextMonkeyFalse: Monkey

        var inspectedItems: Long = 0
            private set

        fun setupTargets(ifTrue: Monkey, ifFalse: Monkey) {
            nextMonkeyTrue = ifTrue
            nextMonkeyFalse = ifFalse
        }

        private var userOp: ((Long) -> Long)? = null

        fun addUserOperation(op: (Long) -> Long) {
            userOp = op
        }

        fun processItems() {
            while (queue.isNotEmpty()) {
                val initialWorry = queue.removeFirst()

                val nextWorry = operation(initialWorry)
                val userReducedWorry = userOp?.invoke(nextWorry) ?: nextWorry

                val nextMonkey = if (userReducedWorry % divisibleBy == 0L) {
                    nextMonkeyTrue
                } else {
                    nextMonkeyFalse
                }

                nextMonkey.queue.addLast(userReducedWorry)

                inspectedItems += 1
            }
        }
    }

    fun String.removePrefixChecked(prefix: String): String {
        require(startsWith(prefix))

        return removePrefix(prefix)
    }

    fun parseOperation(operation: String): (Long) -> Long {
        val rightHand = operation.removePrefixChecked("new = ")

        val (left, op, right) = rightHand.split(" ", limit = 3)

        val longOp: (Long, Long) -> Long = when (op) {
            "+" -> Long::plus
            "*" -> Long::times

            else -> error("Unexpected op '$op'")
        }

        fun toNumberSupplier(refOrNumber: String): (Long) -> Long =
            if (refOrNumber == "old") ({ it }) else ({ refOrNumber.toLong() })

        val leftSupplier: (Long) -> Long = toNumberSupplier(left)
        val rightSupplier: (Long) -> Long = toNumberSupplier(right)

        return { oldValue ->
            longOp(
                leftSupplier(oldValue),
                rightSupplier(oldValue)
            )
        }
    }

    fun parseMonkeys(input: List<String>): List<Monkey> {
        val monkeysAndTargets = input.windowed(size = 6, step = 7) { monkeyLines ->
            val (
                items,
                operation,
                test,
                ifTrue,
                ifFalse
            ) = monkeyLines.drop(1).map { it.trim() }

            Triple(
                Monkey(
                    items.removePrefixChecked("Starting items: ").split(", ").map { it.toLong() },
                    parseOperation(operation.removePrefixChecked("Operation: ")),
                    test.removePrefix("Test: divisible by ").toLong(),
                ),
                ifTrue.removePrefixChecked("If true: throw to monkey ").toInt(),
                ifFalse.removePrefixChecked("If false: throw to monkey ").toInt()
            )
        }

        val monkeys = monkeysAndTargets.map { it.first }

        for ((monkey, ifTrue, ifFalse) in monkeysAndTargets) {
            monkey.setupTargets(monkeys[ifTrue], monkeys[ifFalse])
        }

        return monkeys
    }

    fun evaluateMonkeyBusiness(monkeys: List<Monkey>): Long {
        val processedItems = monkeys.map { it.inspectedItems }.sorted()

        return processedItems.takeLast(2).reduce(Long::times)
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        monkeys.forEach { it.addUserOperation { oldValue -> oldValue / 3 } }

        repeat(20) {
            for (monkey in monkeys) {
                monkey.processItems()
            }
        }

        return evaluateMonkeyBusiness(monkeys)
    }

    fun findCommonDivider(monkeys: List<Monkey>): Long {
        return monkeys.map { it.divisibleBy }.distinct().reduce(Long::times)
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        val commonDivider = findCommonDivider(monkeys)
        monkeys.forEach { it.addUserOperation { oldValue -> oldValue % commonDivider } }

        repeat(10_000) {
            for (monkey in monkeys) {
                monkey.processItems()
            }
        }

        return evaluateMonkeyBusiness(monkeys)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
