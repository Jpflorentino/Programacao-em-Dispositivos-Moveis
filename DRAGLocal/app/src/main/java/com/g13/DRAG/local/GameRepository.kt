package com.g13.DRAG.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.g13.DRAG.history.GameResult
import com.g13.DRAG.history.HistoryDatabase
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val HIGH_SCORE_KEY = "HighScoreKey"

class GameRepository(
        private val sharedPreferences: SharedPreferences,
        private val db: HistoryDatabase
){

    companion object{ //we need a companion object to make sure there is only one instance of worker despite of the number of instances of GameRepository
        private val worker: Executor = Executors.newSingleThreadExecutor()
    }

    var highScore: Int
        get() = sharedPreferences.getInt(HIGH_SCORE_KEY, 0)
        private set(value){
            sharedPreferences
                    .edit()
                    .putInt(HIGH_SCORE_KEY, value)
                    .apply()
        }

    var loadedGames: List<GameResult> = mutableListOf()

    var drawingsList: MutableList<MutableList<Line>> = mutableListOf()

    var wordsList : MutableList<String> = mutableListOf()//all the words input during the round

    var firstWord : String = ""

    var totalPlayers : String = ""

    var totalRounds : String = ""

    var currentRound : Int = 1

    var player1Points : Int = 0

    var playerName: String = ""

    fun changeHighScore(score: Int){
        highScore = score
    }

    fun setBasicParameters(players : String, rounds : String, player: String){
        totalPlayers = players
        totalRounds = rounds
        playerName = player
    }

    fun clear(){
        //clearing the repo before a new game
        drawingsList = mutableListOf()

        wordsList = mutableListOf()

        totalPlayers  = ""

        totalRounds = ""

        player1Points = 0

        currentRound = 1

        playerName = ""
    }

    fun newRound(){
        drawingsList = mutableListOf()

        wordsList = mutableListOf()

        currentRound += 1

        //firstWord = newWord -> no longer needed because the new first word is fetched in the viewModel of the results activity

        //to make the show results activity easier
        wordsList.add(firstWord)
    }

    fun saveResult(){

        worker.execute { //we need a new thread so we don't block the main (UI) thread with database access
            db.getGameResultsDao().insertGame(
                GameResult(
                    players = totalPlayers.toInt(),
                    rounds = totalRounds.toInt(),
                    firstWord = firstWord,
                    words = wordsList,
                    drawings = drawingsList,
                    date = Date(),
                    score = player1Points
                )
            )
        }
    }

    /*fun getAllScores() : LiveData<List<GameResult>> {
        val results = MutableLiveData<List<GameResult>>()
        worker.execute {
            results.postValue(db.getGameResultsDao().loadAllGames())
        }
        return results
    }*/

    /*
    The asynchronous operations return LiveData instead of AsyncTask (deprecated)
     */
    fun getAllScores() : LiveData<List<GameResult>> =
        db.getGameResultsDao().loadAllGames()

    fun getNScores(count: Int): LiveData<List<GameResult>> =
        db.getGameResultsDao().loadLastGames(count)
}