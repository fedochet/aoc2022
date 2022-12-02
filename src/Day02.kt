private enum class GameMove {
    ROCK, PAPER, SCISSORS;

    val winsOver: GameMove
        get() = when (this) {
            ROCK -> SCISSORS
            PAPER -> ROCK
            SCISSORS -> PAPER
        }

    val loosesTo: GameMove
        get() = when (this) {
            SCISSORS -> ROCK
            ROCK -> PAPER
            PAPER -> SCISSORS
        }
}

private enum class GameOutcome {
    WIN, LOOSE, DRAW
}

fun main() {
    fun parseMove(c: Char): GameMove = when (c) {
        'A', 'X' -> GameMove.ROCK
        'B', 'Y' -> GameMove.PAPER
        'C', 'Z' -> GameMove.SCISSORS

        else -> error("Unknown char '$c'")
    }

    fun parseOutcome(c: Char): GameOutcome = when (c) {
        'X' -> GameOutcome.LOOSE
        'Y' -> GameOutcome.DRAW
        'Z' -> GameOutcome.WIN

        else -> error("Unknown char '$c'")
    }

    fun matchResult(player: GameMove, opponent: GameMove): GameOutcome = when {
        player.winsOver == opponent -> GameOutcome.WIN
        player.loosesTo == opponent -> GameOutcome.LOOSE
        player == opponent -> GameOutcome.DRAW

        else -> error("Unexpected combination of moves: $player vs $opponent")
    }

    fun matchScore(player: GameMove, opponent: GameMove): Int {
        val initialScore = when (player) {
            GameMove.ROCK -> 1
            GameMove.PAPER -> 2
            GameMove.SCISSORS -> 3
        }

        val resultScore = when (matchResult(player, opponent)) {
            GameOutcome.WIN -> 6
            GameOutcome.LOOSE -> 0
            GameOutcome.DRAW -> 3
        }

        return initialScore + resultScore
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { match ->
            val (opponentChar, _, playerChar) = match.toList()

            val playerMove = parseMove(playerChar)
            val opponentMove = parseMove(opponentChar)

            matchScore(playerMove, opponentMove)
        }
    }

    fun moveForOutcome(outcome: GameOutcome, opponent: GameMove): GameMove = when (outcome) {
        GameOutcome.DRAW -> opponent
        GameOutcome.WIN -> opponent.loosesTo
        GameOutcome.LOOSE -> opponent.winsOver
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { match ->
            val (opponentChar, _, outcomeChar) = match.toList()

            val opponentMove = parseMove(opponentChar)
            val outcome = parseOutcome(outcomeChar)

            val playerMove = moveForOutcome(outcome, opponentMove)

            matchScore(playerMove, opponentMove)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
