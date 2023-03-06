package minesweeper

import kotlin.math.abs
import kotlin.random.Random

class MineField(val numRows: Int, val numColumns: Int) {
    private var mineField = mutableListOf<MutableList<MutableList<Char>>>()
    private var numMines = 5
    var hasLost = false

    init {
        initializeMineField()
    }

    private fun initializeMineField() {
        generateMineField()
        plantMines()
        setCellNumbers()
    }

    private fun generateMineField() {
        for (row in 0 until numRows) {
            mineField.add(mutableListOf())
            for (column in 0 until numColumns) {
                mineField[row].add(mutableListOf('.', '.'))
            }
        }
    }

    private fun plantMines() {
        var minesOnField = 0
        while (minesOnField < numMines) {
            val randomRow = Random.nextInt(0, numRows)
            val randomColumn = Random.nextInt(0, numColumns)

            if (mineField[randomRow][randomColumn][0] == '.') {
                mineField[randomRow][randomColumn][0] = 'X'
                minesOnField++
                continue
            }
        }
    }

    private fun setCellNumbers() {
        for (row in mineField.indices) {
            for (column in mineField[row].indices) {
                if (mineField[row][column][0] == 'X') continue

                val numSurroundingMines = calculateSurroundingMines(row, column)
                mineField[row][column][0] = numSurroundingMines
            }
        }
    }

    private fun calculateSurroundingMines(currentRow: Int, currentColumn: Int): Char {
        var numSurroundingMines = 0
        for (row in -1..1) {
            for (column in -1..1) {
                try {
                    if (mineField[currentRow + row][currentColumn + column][0] == 'X') numSurroundingMines++
                }
                catch (e: Exception) {
                    continue
                }
            }
        }
        return if (numSurroundingMines == 0) '.'
        else '0' + numSurroundingMines
    }

    private fun getSurroundingCells(currentRow: Int, currentColumn: Int): MutableList<MutableList<Int>> {
        val surroundingCells = mutableListOf<MutableList<Int>>()

        for (row in -1..1) {
            for (column in -1..1) {
                if (row == 0 && column == 0) continue
                if (abs(row) + abs(column) > 1) continue
                try {
                    mineField[currentRow + row][currentColumn + column]
                    surroundingCells.add(mutableListOf(currentRow + row, currentColumn + column))
                }
                catch (e: Exception) {
                    continue
                }
            }
        }
        return surroundingCells
    }

    private fun exploreCell(row: Int, column: Int, exploredCells: MutableList<MutableList<Int>>) {
        if (mineField[row][column][0] == '.') { // true if cell has no surrounding mines
            mineField[row][column][1] = '/'
            exploreSurroundingCells(getSurroundingCells(row, column), exploredCells)
        }
        else if (mineField[row][column][0] != 'X') { // true if cell is empty with surrounding mines
            val numSurroundingMines: Char = mineField[row][column][0]
            mineField[row][column][1] = numSurroundingMines
        }
        else if (isFirstMove() && mineField[row][column][0] == 'X') {
            decrementAdjacentCells(row, column)
            mineField[row][column][0] = '.'
            mineField[row][column][1] = '/'

            val cell = mutableListOf(row, column)
            if (cell in exploredCells) return
            exploredCells.add(cell)
            exploreSurroundingCells(getSurroundingCells(row, column), exploredCells)
        }
    }

    private fun exploreSurroundingCells(surroundingCells: MutableList<MutableList<Int>>, exploredCells: MutableList<MutableList<Int>>) {
        for (cell in surroundingCells) {
            if (cell in exploredCells) continue
            exploredCells.add(cell)
            val row = cell[0]
            val column = cell[1]

            exploreCell(row, column, exploredCells)
        }
    }

    private fun decrementAdjacentCells(currentRow: Int, currentColumn: Int) {
        for (row in -1..1) {
            for (column in -1..1) {
                try {
                    if (mineField[currentRow + row][currentColumn + column][0] != '.' && mineField[currentRow + row][currentColumn + column][0] != 'X') {
                        val decrementedNum = mineField[currentRow + row][currentColumn + column][0].toString().toInt() - 1
                        val decrementedChar: Char = if (decrementedNum < 1) { '.' } else { decrementedNum.toString().first() }
                        mineField[currentRow + row][currentColumn + column][0] = decrementedChar
                    }
                }
                catch (e: Exception) {
                    continue
                }
            }
        }
    }

    fun free(row: Int, column: Int) {
        if (!isFirstMove() && mineField[row][column][0] == 'X') {
            hasLost = true
        }
        val exploredCells = mutableListOf<MutableList<Int>>()
        exploreCell(row, column, exploredCells)
    }

    fun mine(row: Int, column: Int) {
        if (mineField[row][column][1] == '.') {
            mineField[row][column][1] = '*'
        }
        else if (mineField[row][column][1] == '*') {
            mineField[row][column][1] = '.'
        }
    }

    private fun hasFoundAllMines(): Boolean {
        for (row in mineField.indices) {
            for (column in mineField[row].indices) {
                if (mineField[row][column][0] == 'X') {
                    if (mineField[row][column][1] != '*') return false
                }

                if (mineField[row][column][0] != 'X') {
                    if (mineField[row][column][1] == '*') return false
                }
            }
        }
        return true
    }

    private fun hasExploredAllFreeCells(): Boolean {
        for (row in mineField.indices) {
            for (column in mineField[row].indices) {
                if (mineField[row][column][0] != 'X') {
                    if (mineField[row][column][1] == '.') return false
                }
            }
        }
        return true
    }

    private fun isFirstMove(): Boolean {
        for (row in mineField.indices) {
            for (column in mineField[row].indices) {
                if (mineField[row][column][1] != '.' && mineField[row][column][1] != '*') return false
            }
        }
        return true
    }

    fun showAllMines() {
        for (row in mineField.indices) {
            for (column in mineField[row].indices) {
                if (mineField[row][column][0] == 'X') {
                    mineField[row][column][1] = 'X'
                }
            }
        }
    }

    fun hasWon(): Boolean {
        return hasFoundAllMines() || hasExploredAllFreeCells()
    }

    fun print() {
        var columnNumberString = ""
        for (num in 1 .. numColumns) {
            columnNumberString += num
        }
        columnNumberString = " |$columnNumberString|"
        println(columnNumberString)
        println("-|${"*".repeat(numColumns)}|")

        for (row in mineField.indices) {
            var rowString = ""
            for (column in mineField[row].indices) {
                rowString += mineField[row][column][1]
            }
            println("${row + 1}|${rowString}|")
        }
        println("-|${"*".repeat(numColumns)}|")
    }
}