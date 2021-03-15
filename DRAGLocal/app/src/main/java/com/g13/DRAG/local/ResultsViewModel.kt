package com.g13.DRAG.local

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g13.DRAG.HighScores.HighScoreInfo
import com.g13.DRAG.HighScores.Result
import com.g13.DRAG.HighScores.State
import com.g13.DRAG.history.GameResult

class ResultsViewModel(application: Application) : AndroidViewModel(application){

    private val app by lazy { application as GameApplication }

    private val gameRepository by lazy{app.gameRepository}

    private val highScoresRepository by lazy {app.highscoresRepository}

    val publishedScore: LiveData<Result<HighScoreInfo, Exception>> = MutableLiveData()

    fun checkPoints(gotPoints: String, noPoints: String) : String{

        lateinit var pointsString: String

        //we don't use the firstWord in the if because in the Results activity we update the property before doing the check, which makes it always return false
        if(gameRepository.wordsList[gameRepository.wordsList.lastIndex] == gameRepository.wordsList[0]){

            gameRepository.player1Points += 1

            pointsString = gotPoints

        }else{

            pointsString = noPoints

        }

        return pointsString
    }

    //save the match in permanent storage
    // (this action can't use the main thread so it has to be called in the viewModel)
    fun saveGame() {

        gameRepository.saveResult()

        if (gameRepository.player1Points > gameRepository.highScore) {

            gameRepository.changeHighScore(gameRepository.player1Points)

            val mutableResult = publishedScore as MutableLiveData<Result<HighScoreInfo, Exception>>

            highScoresRepository.publishHighScore(
                gameRepository.playerName,
                gameRepository.highScore.toString(),
                gameRepository.wordsList[0],
                onSuccess = { mutableResult.value = Result(State.COMPLETE, result = it) },
                onError = { mutableResult.value = Result(State.COMPLETE, error = it) }
            )
        }
    }

    fun saveLoadedGameIntoRepo(game: GameResult){
        Log.v("loadedGames", "game: ${game.toString()}")
        getApplication<GameApplication>().gameRepository.totalPlayers = game.players.toString()
        getApplication<GameApplication>().gameRepository.wordsList = game.words as MutableList<String>
        getApplication<GameApplication>().gameRepository.drawingsList = game.drawings as MutableList<MutableList<Line>>
        getApplication<GameApplication>().gameRepository.totalRounds = game.rounds.toString()
    }
}