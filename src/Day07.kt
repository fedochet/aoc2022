sealed interface FSEntry {
    val name: String
    val parent: FSEntry?
}

data class FSFile(override val name: String, val size: Int, override val parent: FSEntry?) : FSEntry

class FSDir(override val name: String, override val parent: FSDir?) : FSEntry {
    private val entries = mutableMapOf<String, FSEntry>()

    fun add(entry: FSEntry) {
        entries[entry.name] = entry
    }

    fun get(name: String): FSEntry? {
        return entries[name]
    }

    val children: Collection<FSEntry>
        get() = entries.values
}

fun main() {
    fun countDirectorySizes(start: FSDir): Sequence<Int> {
        /**
         * Yields sizes of all directories inside [dir] including [dir] itself.
         *
         * @return the total size of the [dir] directory.
         */
        suspend fun SequenceScope<Int>.traverseAndCountSize(dir: FSDir): Int {
            var totalSize = 0

            for (child in dir.children) {
                totalSize += when (child) {
                    is FSDir -> traverseAndCountSize(child)

                    is FSFile -> child.size
                }
            }

            yield(totalSize)

            return totalSize
        }

        return sequence { traverseAndCountSize(start) }
    }

    fun parseCommands(input: List<String>): List<Pair<String, List<String>>> {
        val commandsPositions = input.mapIndexedNotNull { idx, line ->
            if (line.startsWith("$")) idx else null
        }

        return (commandsPositions + input.size)
            .zipWithNext()
            .map { (from, to) -> input.subList(from, to) }
            .map { it.first().removePrefix("$ ") to it.drop(1) }
    }

    fun parseLsEntry(entry: String, currentDir: FSDir): FSEntry {
        return if (entry.startsWith("dir")) {
            val dirName = entry.removePrefix("dir ")
            FSDir(dirName, parent = currentDir)
        } else {
            val (size, name) = entry.split(" ", limit = 2)
            FSFile(name, size.toInt(), parent = currentDir)
        }
    }

    fun buildFileTree(commandsWithResults: List<Pair<String, List<String>>>): FSDir {
        val root = FSDir("/", parent = null)
        var currentDir = root

        for ((command, result) in commandsWithResults) {
            when {
                command.startsWith("cd") -> {
                    require(result.isEmpty())

                    val target = command.removePrefix("cd ")
                    currentDir = when (target) {
                        "/" -> root
                        ".." -> currentDir.parent ?: error("No parent")

                        else -> (currentDir.get(target) ?: error("No such directory")) as? FSDir
                            ?: error("Not a directory")
                    }
                }

                command.startsWith("ls") -> {
                    val entries = result.map { entry -> parseLsEntry(entry, currentDir) }

                    entries.forEach { currentDir.add(it) }
                }

                else -> error("Unexpected command '$command'")
            }
        }
        return root
    }

    fun part1(input: List<String>): Int {
        val commandsWithResults = parseCommands(input)

        val root = buildFileTree(commandsWithResults)

        return countDirectorySizes(root).filter { size -> size <= 100_000 }.sum()
    }

    fun part2(input: List<String>): Int {
        val commandsWithResults = parseCommands(input)

        val root = buildFileTree(commandsWithResults)

        val totalDiskSpace = 70_000_000
        val targetFreeSpace = 30_000_000

        val occupiedSpace = countDirectorySizes(root).last()
        val freeSpace = totalDiskSpace - occupiedSpace

        require(freeSpace < targetFreeSpace)

        val spaceToClean = targetFreeSpace - freeSpace

        return countDirectorySizes(root)
            .filter { it >= spaceToClean }
            .min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24_933_642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}