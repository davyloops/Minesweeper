package minesweeper

class Game {
    private lateinit var mineField: MineField

    fun initGame() {
        printInstructions()
        val numRows = getMineFieldNumRows()
        val numColumns = getMineFieldNumColumns()
        mineField = MineField(numRows, numColumns)
    }

    private fun printInstructions() {
        println("Hello! Welcome to minesweeper.")
        println("Commands are entered in the following format: row column keyword. For example: 6 3 mine")
        println("The available keywords are mine and free.")
        println("The mine keyword allows you to mark spots where you think a mine might be. These will be shown as a \"*\".")
        println("The free keyword allows you to explore a cell. If there is a mine there, you lose the game.")
        println("Unexplored cells contain a \".\". Explored cells that do not contain a mine have a \"/\".")
    }

    fun playGame() {
        while (!mineField.hasLost && !mineField.hasWon()) {
            playRound()
        }
        endGame()
    }

    private fun playRound() {
        mineField.print()
        println("Explore a cell (free) or mark a possible mine (mine):")
        val input = readln().split(" ")
        val column = try {input[0].toInt() - 1} catch(e: Exception) {null}
        val row = try {input[1].toInt() - 1} catch(e: Exception) {null}
        val keyword = try {input[2]} catch(e: Exception) {null}

        if (!validateInput(row, column, keyword)) {
            printInputError()
            return
        }

        when (keyword) {
            "free" -> { mineField.free(row!!, column!!) }
            "mine" -> { mineField.mine(row!!, column!!) }
        }
    }

    private fun getMineFieldNumRows(): Int {
        println("Enter the desired number of rows:")
        while (true) {
            val input = readln()
            var numRows = input.toIntOrNull()

            if (numRows != null) return numRows

            println("Please enter a whole number greater than 1.")
            println("Enter the desired number of rows:")
        }
    }

    private fun getMineFieldNumColumns(): Int {
        println("Enter the desired number of columns:")
        while (true) {
            val input = readln()
            var numColumns = input.toIntOrNull()

            if (numColumns != null) return numColumns

            println("Please enter a whole number greater than 1.")
            println("Enter the desired number of columns:")
        }
    }

    private fun printInputError() {
        println("Please enter cell in row column keyword format. Available keywords are mine and free.")
        println("Example: 3 5 free")
    }

    private fun validateInput(column: Int?, row: Int?, keyword: String?): Boolean {
        if (row != null && row !in 0 until mineField.numRows) return false
        if (column != null && column !in 0 until mineField.numColumns) return false
        if (keyword != null && keyword != "mine" && keyword != "free") return false

        return true
    }

    private fun endGame() {
        if (mineField.hasWon()) {
            mineField.print()
            println("You found all the mines! Congratulations!")
        }
        else {
            mineField.showAllMines()
            mineField.print()
            println("You stepped on a mine!")
        }
    }

    fun shouldPlayAgain(): Boolean {
        println("Would you like to play again? (yes/no)")
        var playAgainInput = try {readln().lowercase()} catch (e:Exception) {null}
        if (playAgainInput == "y" || playAgainInput == "yes") return true
        return false
    }
}