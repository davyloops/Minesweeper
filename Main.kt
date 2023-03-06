package minesweeper

fun main() {
    var shouldPlayAgain = false
    do {
        val game = Game()
        game.initGame()
        game.playGame()
        shouldPlayAgain = game.shouldPlayAgain()
    } while (shouldPlayAgain)
}