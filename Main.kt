package minesweeper

import kotlin.random.Random

var NUM_ROWS = 9
var NUM_COLUMNS = 9
var NUM_MINES = 5
var MINE_FIELD: MutableList<String> = mutableListOf()
var PLAYER_MINE_FIELD: MutableList<String> = mutableListOf()

fun main() {
    initGame()
    playGame()
}

fun initGame() {
    println("Hello! Welcome to minesweeper.")
    println("Commands are entered in the following format: row column keyword. For example: 6 3 mine")
    println("The available keywords are mine and free.")
    println("The mine keyword allows you to mark spots where you think a mine might be. These will be shown as a \"*\".")
    println("The free keyword allows you to explore a cell. If there is a mine there, you lose the game.")
    println("Unexplored cells contain a \".\". Explored cells that do not contain a mine have a \"/\".")

    MINE_FIELD = generateMineField()
    plantMines()
    setCellNumbers()
    setPlayerMineField()
}

fun playGame() {
    while (!hasWonGame()) {
        printPlayerMineField()
        println("Set/unset mine marks or claim a cell as free:")
        val input = readln().split(" ")
        var row: Int
        var column: Int
        var keyword: String

        try {
            row = input[0].toInt() - 1
            column = input[1].toInt() - 1
            keyword = input[2]
        }
        catch (e: Exception) {
            printInputError()
            println("exception")
            continue
        }

        if (!validateInput(row, column, keyword)) {
            printInputError()
            continue
        }

        when (keyword) {
            "free" -> { free(row, column) }
            "mine" -> { mine(row, column) }
        }
    }
    printPlayerMineField()
    println("Congratulations! You found all the mines!")
}

fun printInputError() {
    println("Please enter cell in row column keyword format. Available keywords are mine and free.")
    println("Example: 3 5 free")
}

fun validateInput(row: Int, column: Int, keyword: String): Boolean {
    if (row !in 0..NUM_ROWS - 1) return false
    if (column !in 0..NUM_COLUMNS - 1) return false
    if (keyword != "mine" && keyword != "free") return false
    return true
}

fun generateMineField(): MutableList<String> {
    val mineField = mutableListOf<String>()

    for (row in 1..NUM_ROWS) {
        var rowString = ""
        for (column in 1..NUM_COLUMNS) {
            rowString += "."
        }
        mineField.add(rowString)
    }
    return mineField
}

fun plantMines() {
    var minesOnField = 0
    while (minesOnField < NUM_MINES) {
        val randomRow = Random.nextInt(0, NUM_ROWS)
        val randomColumn = Random.nextInt(0, NUM_COLUMNS)

        if (MINE_FIELD[randomRow][randomColumn] == '.') {
            val string = MINE_FIELD[randomRow]
            val chars = string.toCharArray()
            chars[randomColumn] = 'X'
            MINE_FIELD[randomRow] = String(chars)
            minesOnField++
            continue
        }
    }
}

fun setCellNumbers() {
    for (row in MINE_FIELD.indices) {
        for (column in MINE_FIELD[row].indices) {
            if (MINE_FIELD[row][column] == 'X') continue

            val numSurroundingMines = calculateSurroundingMines(row, column)
            val chars = MINE_FIELD[row].toCharArray()
            chars[column] = numSurroundingMines
            MINE_FIELD[row] = String(chars)
        }
    }
}

fun calculateSurroundingMines(currentRow: Int, currentColumn: Int): Char {
    var numSurroundingMines = 0
    for (row in -1..1) {
        for (column in -1..1) {
            try {
                if (MINE_FIELD[currentRow + row][currentColumn + column] == 'X') numSurroundingMines++
            }
            catch (e: Exception) {
                continue
            }
        }
    }
    return if (numSurroundingMines == 0) '.'
    else '0' + numSurroundingMines
}

fun setPlayerMineField() {
    PLAYER_MINE_FIELD = MINE_FIELD.toMutableList()

    for (row in PLAYER_MINE_FIELD.indices) {
        if (row > NUM_ROWS - 1) continue
        for (column in PLAYER_MINE_FIELD[row].indices) {
            if (column > NUM_COLUMNS - 1) continue
            changePlayerMineFieldCell(row, column, '.')
        }
    }
}

fun printPlayerMineField() {
    println(" |123456789|")
    println("-|---------|")

    for (row in PLAYER_MINE_FIELD.indices) {
        println("${row + 1}|${PLAYER_MINE_FIELD[row]}|")
    }
    println("-|---------|")
}

fun isFirstMove(): Boolean {
    for (row in PLAYER_MINE_FIELD.indices) {
        for (column in PLAYER_MINE_FIELD[row].indices) {
            if (PLAYER_MINE_FIELD[row][column] != '.' && PLAYER_MINE_FIELD[row][column] != '*') return false
        }
    }
    return true
}

fun getSurroundingCells(currentRow: Int, currentColumn: Int): MutableList<MutableList<Int>> {
    val surroundingCells = mutableListOf<MutableList<Int>>()

    for (row in -1..1) {
        for (column in -1..1) {
            try {
                MINE_FIELD[currentRow + row][currentColumn + column]
                surroundingCells.add(mutableListOf<Int>(currentRow + row, currentColumn + column))
            }
            catch (e: Exception) {
                continue
            }
        }
    }
    return surroundingCells
}

fun exploreSurroundingCells(surroundingCells: MutableList<MutableList<Int>>, exploredCells: MutableList<MutableList<Int>>) {
    for (cell in surroundingCells) {
        if (cell in exploredCells) continue
        exploredCells.add(cell)
        val row = cell[0]
        val column = cell[1]

        exploreCell(row, column, exploredCells)
    }
}

fun exploreCell(row: Int, column: Int, exploredCells: MutableList<MutableList<Int>>) {
    if (MINE_FIELD[row][column] == '.') { // true if cell has no surrounding mines
        changePlayerMineFieldCell(row, column, '/')
        exploreSurroundingCells(getSurroundingCells(row, column), exploredCells)
    }
    else if (MINE_FIELD[row][column] != '.' && MINE_FIELD[row][column] != 'X') { // true if cell is empty with surrounding mines
        val numSurroundingMines: Char = MINE_FIELD[row][column]
        println(numSurroundingMines)
        changePlayerMineFieldCell(row, column, numSurroundingMines)
    }
    else if (isFirstMove() && MINE_FIELD[row][column] == 'X') {
        println("first move")
        decrementAdjacentCells(row, column)

        val chars = MINE_FIELD[row].toCharArray()
        chars[column] = '.'
        MINE_FIELD[row] = String(chars)

        changePlayerMineFieldCell(row, column, '/')
        val cell = mutableListOf<Int>(row, column)
        if (cell in exploredCells) return
        exploredCells.add(cell)
        exploreSurroundingCells(getSurroundingCells(row, column), exploredCells)
    }
}

private fun decrementAdjacentCells(currentRow: Int, currentColumn: Int) {
    for (row in -1..1) {
        for (column in -1..1) {
            try {
                if (MINE_FIELD[currentRow + row][currentColumn + column] != '.' && MINE_FIELD[currentRow + row][currentColumn + column] != 'X') {
                    var num = MINE_FIELD[currentRow + row][currentColumn + column].toString().toInt() - 1
                    val char: Char = if (num < 1) { '.' } else { num.toString().first() }
                    val chars = MINE_FIELD[currentRow + row].toCharArray()
                    chars[currentColumn + column] = char
                    MINE_FIELD[currentRow + row] = String(chars)
                }
            }
            catch (e: Exception) {
                continue
            }
        }
    }
}

fun changePlayerMineFieldCell(row: Int, column: Int, char: Char) {
    val chars = PLAYER_MINE_FIELD[row].toCharArray()
    chars[column] = char
    PLAYER_MINE_FIELD[row] = String(chars)
}

fun free(row: Int, column: Int) {
    if (!isFirstMove() && MINE_FIELD[row][column] == 'X') {
        loseGame()
    }
    val exploredCells = mutableListOf<MutableList<Int>>()
    exploreCell(row, column, exploredCells)
}

fun mine(row: Int, column: Int) {
    if (PLAYER_MINE_FIELD[row][column] == '.') {
        changePlayerMineFieldCell(row, column, '*')
    }
    else if (PLAYER_MINE_FIELD[row][column] == '*') {
        changePlayerMineFieldCell(row, column, '.')
    }

}

fun loseGame() {
    for (row in MINE_FIELD.indices) {
        for (column in MINE_FIELD[row].indices) {
            if (MINE_FIELD[row][column] == 'X') {
                changePlayerMineFieldCell(row, column, 'X')
            }
        }
    }

    printPlayerMineField()
    println("You stepped on a mine and failed!")
}

fun hasFoundAllMines(): Boolean {
    for (row in MINE_FIELD.indices) {
        for (column in MINE_FIELD[row].indices) {
            if (MINE_FIELD[row][column] == 'X') {
                if (PLAYER_MINE_FIELD[row][column] != '*') return false
            }

            if (MINE_FIELD[row][column] != 'X') {
                if (PLAYER_MINE_FIELD[row][column] == '*') return false
            }
        }
    }
    return true
}

fun hasExploredAllFreeCells(): Boolean {
    for (row in MINE_FIELD.indices) {
        for (column in MINE_FIELD[row].indices) {
            if (MINE_FIELD[row][column] != 'X') {
                if (PLAYER_MINE_FIELD[row][column] == '.') return false
            }
        }
    }
    return true
}

fun hasWonGame(): Boolean {
    return hasFoundAllMines() || hasExploredAllFreeCells()
}