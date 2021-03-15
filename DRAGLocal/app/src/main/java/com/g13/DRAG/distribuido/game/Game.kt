package com.g13.DRAG.distribuido.game

import android.os.Parcelable
import com.g13.DRAG.local.Line
import kotlinx.android.parcel.Parcelize

data class GameDTO(
    val firstWord: String,
    val wordList: Array<String>,
    val currentDrawing: Array<Line>,
    val turn: String,
    val state: Game.State,
    val totalPlayers: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameDTO

        if (firstWord != other.firstWord) return false
        if (!currentDrawing.contentEquals(other.currentDrawing)) return false
        if (turn != other.turn) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstWord.hashCode()
        result = 31 * result + currentDrawing.contentHashCode()
        result = 31 * result + (turn?.hashCode() ?: 0)
        result = 31 * result + state.hashCode()
        return result
    }
}

fun GameDTO.toGame() = Game(firstWord, wordList.toMutableList(), currentDrawing.toMutableList(), turn, state, totalPlayers)

@Parcelize
data class Game(
    private var firstWord: String = "",
    private var wordList: MutableList<String> = mutableListOf(),
    private val currentDrawing: MutableList<Line> = mutableListOf(),
    private var turn: String = "",
    private var currState: State = State.DRAWING,
    private var totalPlayers: String = ""
) : Parcelable {

    enum class State {DRAWING, WRITING}

    fun toGameDTO() = GameDTO(firstWord, wordList.toTypedArray(), currentDrawing.toTypedArray() , turn, currState, totalPlayers)
}