private sealed interface MonkeyTask
private data class NumberTask(val number: Long) : MonkeyTask
private data class OpTask(val left: String, val op: String, val right: String) : MonkeyTask

private const val ROOT = "root"
private const val HUMAN = "humn"

fun main() {
    fun compute(left: Long, op: String, right: Long) = when (op) {
        "+" -> left + right
        "-" -> left - right
        "*" -> left * right
        "/" -> left / right

        else -> error("Unknown op $op")
    }

    fun evalMonkey(monkeys: Map<String, MonkeyTask?>, name: String): Long? {
        val task = monkeys.getValue(name) ?: return null

        return when (task) {
            is NumberTask -> task.number
            is OpTask -> {
                val left = evalMonkey(monkeys, task.left) ?: return null
                val right = evalMonkey(monkeys, task.right) ?: return null

                compute(left, task.op, right)
            }
        }
    }

    fun parseMonkeys(input: List<String>) = input.associate {
        val (name, taskStr) = it.split(": ", limit = 2)

        val taskTokens = taskStr.split(" ")
        val task = if (taskTokens.size == 1) {
            NumberTask(taskTokens.single().toLong())
        } else {
            val (left, op, right) = taskTokens
            OpTask(left, op, right)
        }

        name to task
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        return evalMonkey(monkeys, ROOT) ?: error("Must be computable")
    }

    fun decompute(left: Long?, op: String, right: Long?, expectedResult: Long): Long {
        require((left == null) != (right == null))

        return when (op) {
            "+" -> {
                if (left == null) {
                    expectedResult - right!!
                } else {
                    expectedResult - left
                }
            }
            "-" -> {
                if (left == null) {
                    expectedResult + right!!
                } else {
                    left - expectedResult
                }
            }
            "*" -> {
                if (left == null) {
                    expectedResult / right!!
                } else {
                    expectedResult / left
                }
            }

            "/" -> {
                if (left == null) {
                    expectedResult * right!!
                } else {
                    left / expectedResult
                }
            }

            else -> error("Unknown op $op")
        }
    }

    fun guessMissingMonkeyValue(monkeys: Map<String, MonkeyTask?>, monkey: String, target: Long): Long {
        val task = monkeys.getValue(monkey)
        if (task == null) return target

        val (leftM, op, rightM) = task as OpTask

        val left = evalMonkey(monkeys, leftM)
        val right = evalMonkey(monkeys, rightM)

        val nextTarget = decompute(left, op, right, target)

        return when {
            left != null -> guessMissingMonkeyValue(monkeys, rightM, nextTarget)
            right != null -> guessMissingMonkeyValue(monkeys, leftM, nextTarget)

            else -> error("None of $left or $right is computable")
        }
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input).mapValues { (name, task) -> if (name == HUMAN) null else task }

        val (leftM, _, rightM) = monkeys.getValue(ROOT) as OpTask

        val left = evalMonkey(monkeys, leftM)
        val right = evalMonkey(monkeys, rightM)

        return when {
            left != null -> guessMissingMonkeyValue(monkeys, rightM, left)
            right != null -> guessMissingMonkeyValue(monkeys, leftM, right)

            else -> error("Both root branches are not computable")
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}