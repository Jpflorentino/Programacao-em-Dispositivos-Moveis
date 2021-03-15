package com.g13.DRAG.distribuido.game

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g13.DRAG.distribuido.ChallengeInfo
import com.g13.DRAG.local.*

/**
* The Game view model
*/
class GameViewModel(
    app: Application,
    val localPlayer: Int,
    val challengeInfo: ChallengeInfo
) : AndroidViewModel(app) {

    private val subscription = getApplication<GameApplication>().distributedRepository.subscribeTo(
        challengeInfo.id,
        onSubscriptionError = { TODO() },
        onStateChanged = { (gameState as MutableLiveData<Game>).value = it
        }
    )

    override fun onCleared() {
        super.onCleared()
        Log.v(TAG, "GameViewModel.onCleared()")
        subscription.remove()
    }

    val gameState: LiveData<Game> = MutableLiveData(Game())

    private val app by lazy { app as GameApplication }

    lateinit var stroke: MutableList<Point> //Current line of the drawing

    lateinit var line: Line

    var currentRound: Int = 1

    var totalRounds = challengeInfo.totalRounds

    val totalPlayers = challengeInfo.playersEnrolled.toInt() + 1

    val game: MutableLiveData<GameState> by lazy {
        MutableLiveData<GameState>(GameState())
    }

    var currentDrawing: MutableList<Line> = mutableListOf()

    val wordList: MutableList<String> = mutableListOf(challengeInfo.firstWord)

    var isScheduled = false

    val scheduleComplete: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun runDelayed(millis: Long, action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(action, millis)
    }

    fun scheduleTransition(millis: Long) {
        if (!isScheduled) {
            isScheduled = true
            runDelayed(millis) {
                scheduleComplete.value = true
            }
        }
    }

    fun changeState(word : String) : GameViewModel { //not really storing any important value in firestore atm

        isScheduled = false

        scheduleComplete.value = false

        if(game.value?.state == GameState.State.DRAWING){

            game.value = GameState(mutableListOf(), game.value?.turn!! + 1, GameState.State.WRITING)

            app.distributedRepository.updateGameState(gameState.value ?: throw IllegalStateException(),
                challengeInfo,
                onSuccess = { (gameState as MutableLiveData<Game>).value = it },
                onError = { }//TODO()
            )

        } else {

            wordList.add(word)

            currentDrawing = mutableListOf()

            game.value = GameState(mutableListOf(), game.value?.turn!! + 1, GameState.State.DRAWING)

            app.distributedRepository.updateGameState(gameState.value ?: throw IllegalStateException(),
                challengeInfo,
                onSuccess = { (gameState as MutableLiveData<Game>).value = it },
                onError = {} //TODO() }
            )
        }
        return this
    }

    fun removeChallenge(challengeID: String){
        app.distributedRepository.unpublishChallenge(challengeID,
            onSuccess = { Log.v(TAG, "unpublished sucessfully") },
            onError = {Log.v(TAG, "could not unpublish")})
    }
}