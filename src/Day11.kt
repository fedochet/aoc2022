fun main() {
    class Monkey(
        startingItems: List<Int>,
        private val operation: (Int) -> Int,
        private val divisibleBy: Int,
    ) {
        private val queue: ArrayDeque<Int> = ArrayDeque(startingItems)

        private lateinit var nextMonkeyTrue: Monkey
        private lateinit var nextMonkeyFalse: Monkey

        var inspectedItems: Int = 0
            private set

        fun setupTargets(ifTrue: Monkey, ifFalse: Monkey) {
            nextMonkeyTrue = ifTrue
            nextMonkeyFalse = ifFalse
        }

        fun processItems() {
            while (queue.isNotEmpty()) {
                val initialWorry = queue.removeFirst()

                val nextWorry = operation(initialWorry)
                val reducedWorry = nextWorry / 3

                val nextMonkey = if (reducedWorry % divisibleBy == 0) {
                    nextMonkeyTrue
                } else {
                    nextMonkeyFalse
                }

                nextMonkey.queue.addLast(reducedWorry)

                inspectedItems += 1
            }
        }
    }

    fun String.removePrefixChecked(prefix: String): String {
        require(startsWith(prefix))

        return removePrefix(prefix)
    }

    fun parseOperation(operation: String): (Int) -> Int {
        val rightHand = operation.removePrefixChecked("new = ")

        val (left, op, right) = rightHand.split(" ", limit = 3)

        val intOp: (Int, Int) -> Int = when (op) {
            "+" -> Int::plus
            "*" -> Int::times

            else -> error("Unexpected op '$op'")
        }

        val leftSupplier: (Int) -> Int = if (left == "old") ({ it }) else ({ left.toInt() })
        val rightSupplier: (Int) -> Int = if (right == "old") ({ it }) else ({ right.toInt() })

        return { oldValue ->
            intOp(
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
                    items.removePrefixChecked("Starting items: ").split(", ").map { it.toInt() },
                    parseOperation(operation.removePrefixChecked("Operation: ")),
                    test.removePrefix("Test: divisible by ").toInt(),
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

    fun part1(input: List<String>): Int {
        val monkeys = parseMonkeys(input)

        repeat(20) {
            for (monkey in monkeys) {
                monkey.processItems()
            }
        }

        return monkeys
            .map { it.inspectedItems }
            .sorted()
            .takeLast(2)
            .reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605)

    val input = readInput("Day11")
    println(part1(input))
}
