private enum class Mineral {
    ORE, CLAY, OBSIDIAN, GEODE
}

private enum class MiningRobot {
    ORE_ROBOT, CLAY_ROBOT, OBSIDIAN_ROBOT, GEODE_ROBOT
}

fun main() {
    data class Cost(
        val oreCount: Int = 0,
        val clayCount: Int = 0,
        val obsidianCount: Int = 0,
        val geodeCount: Int = 0,
    ) {
        fun forMineral(mineral: Mineral) = when (mineral) {
            Mineral.ORE -> oreCount
            Mineral.CLAY -> clayCount
            Mineral.OBSIDIAN -> obsidianCount
            Mineral.GEODE -> geodeCount
        }
    }

    class Blueprint(
        val id: Int,
        oreRobotCost: Cost,
        clayRobotCost: Cost,
        obsidianRobotCost: Cost,
        geodeRobotCost: Cost
    ) {
        val costs: Map<MiningRobot, Cost>

        init {
            costs = mapOf(
                MiningRobot.ORE_ROBOT to oreRobotCost,
                MiningRobot.CLAY_ROBOT to clayRobotCost,
                MiningRobot.OBSIDIAN_ROBOT to obsidianRobotCost,
                MiningRobot.GEODE_ROBOT to geodeRobotCost,
            )
        }

        fun costOf(robot: MiningRobot): Cost = costs[robot] ?: error("No price for robot $robot")
    }

    fun parseBlueprint(str: String): Blueprint {
        val pattern = Regex(
            "Blueprint (\\d+): Each ore robot costs (\\d+) ore. " +
                    "Each clay robot costs (\\d+) ore. " +
                    "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
                    "Each geode robot costs (\\d+) ore and (\\d+) obsidian\\."
        )

        val (
            id,
            oreRobotOre,
            clayRobotOre,
            obsidianRobotOre,
            obsidianRobotClay,
            geodeRobotOre,
            geodeRobotObsidian,
        ) = pattern.matchEntire(str)?.destructured ?: error("Cannot match string: '$str'")

        return Blueprint(
            id.toInt(),
            Cost(oreCount = oreRobotOre.toInt()),
            Cost(oreCount = clayRobotOre.toInt()),
            Cost(oreCount = obsidianRobotOre.toInt(), clayCount = obsidianRobotClay.toInt()),
            Cost(oreCount = geodeRobotOre.toInt(), obsidianCount = geodeRobotObsidian.toInt()),
        )
    }

    data class State(
        val oreRobots: Int,
        val clayRobots: Int,
        val obsidianRobots: Int,
        val geodeRobots: Int,
        val oreCount: Int,
        val clayCount: Int,
        val obsidianCount: Int,
        val geodeCount: Int,
    ) {
        fun crystalsPerTick(): Cost {
            return Cost(
                oreCount = oreRobots,
                clayCount = clayRobots,
                obsidianCount = obsidianRobots,
                geodeCount = geodeRobots,
            )
        }

        fun canBuy(cost: Cost): Boolean {
            return oreCount >= cost.forMineral(Mineral.ORE) &&
                    clayCount >= cost.forMineral(Mineral.CLAY) &&
                    obsidianCount >= cost.forMineral(Mineral.OBSIDIAN) &&
                    geodeCount >= cost.forMineral(Mineral.GEODE)
        }

        fun addCost(cost: Cost): State = copy(
            oreCount = (oreCount + cost.forMineral(Mineral.ORE)),
            clayCount = (clayCount + cost.forMineral(Mineral.CLAY)),
            obsidianCount = (obsidianCount + cost.forMineral(Mineral.OBSIDIAN)),
            geodeCount = (geodeCount + cost.forMineral(Mineral.GEODE)),
        )

        fun removeCost(cost: Cost): State = copy(
            oreCount = (oreCount - cost.forMineral(Mineral.ORE)),
            clayCount = (clayCount - cost.forMineral(Mineral.CLAY)),
            obsidianCount = (obsidianCount - cost.forMineral(Mineral.OBSIDIAN)),
            geodeCount = (geodeCount - cost.forMineral(Mineral.GEODE)),
        )

        fun addRobot(robot: MiningRobot): State = when (robot) {
            MiningRobot.ORE_ROBOT -> copy(oreRobots = (oreRobots + 1))
            MiningRobot.CLAY_ROBOT -> copy(clayRobots = (clayRobots + 1))
            MiningRobot.OBSIDIAN_ROBOT -> copy(obsidianRobots = (obsidianRobots + 1))
            MiningRobot.GEODE_ROBOT -> copy(geodeRobots = (geodeRobots + 1))
        }
    }

    fun nextStates(originalState: State, blueprint: Blueprint): List<State> = buildList {
        val crystals = originalState.crystalsPerTick()
        val baseNextState = originalState.addCost(crystals)

        fun yield(s: State) = add(s)

        var canBuyRobots = 0

        for (robot in MiningRobot.values()) {
            val robotCost = blueprint.costOf(robot)
            if (originalState.canBuy(robotCost)) {
                yield(baseNextState.removeCost(robotCost).addRobot(robot))
                canBuyRobots++
            }
        }

        if (canBuyRobots != MiningRobot.values().size) {
            yield(baseNextState)
        }
    }

    fun evaluate2(blueprint: Blueprint, time: Int): Int {
        var states = listOf(
            State(
                oreRobots = 1,
                clayRobots = 0,
                obsidianRobots = 0,
                geodeRobots = 0,
                oreCount = 0,
                clayCount = 0,
                obsidianCount = 0,
                geodeCount = 0,
            )
        )

        repeat(time) { currentTime ->
            println("time: $currentTime, states number: ${states.size}")

            val newStates = mutableListOf<State>()
            for (state in states) {
                newStates += nextStates(state, blueprint)
            }

            // This is very dirty hack
            states = if (currentTime < 25) {
                newStates.distinct()
            } else {
                newStates.distinct().sortedByDescending { it.geodeCount }.take(newStates.size / 2)
            }
        }

        return states.maxOf { it.geodeCount }.also { println("blueprint ${blueprint.id}, max is $it") }
    }

    fun part1(input: List<String>): Int {
        val blueprints = input.map { parseBlueprint(it) }

        return blueprints.map { evaluate2(it, time = 24) * it.id }.sum()
    }

    fun part2(input: List<String>): Int {
        val blueprints = input.map { parseBlueprint(it) }.take(3)

        return blueprints.map { evaluate2(it, time = 32) }.reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 3472)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}