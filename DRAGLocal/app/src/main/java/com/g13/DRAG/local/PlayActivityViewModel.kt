package com.g13.DRAG.local

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.g13.DRAG.distribuido.game.GameViewModel
import kotlinx.android.parcel.Parcelize
private const val SAVED_STATE_KEY = "PlayActivityViewModel.SavedState"

@Parcelize
data class GameState(
    val currentDrawing: MutableList<Line> = mutableListOf(),
    val turn: Int = 1,
    val state : State = State.DRAWING
) : Parcelable {

    enum class State { DRAWING, WRITING }
}

class PlayActivityViewModel(application: Application,
                private val savedState: SavedStateHandle
) : AndroidViewModel(application) {

    private val app by lazy { application as GameApplication }

    private val gameRepository by lazy{app.gameRepository}

    lateinit var stroke: MutableList<Point> //Current line of the drawing

    lateinit var line: Line

    val game: MutableLiveData<GameState> by lazy {
        MutableLiveData<GameState>(savedState.get<GameState>(SAVED_STATE_KEY) ?: GameState())
    }

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

    fun changeState(word : String) : PlayActivityViewModel {

        isScheduled = false

        scheduleComplete.value = false

        if(game.value?.state == GameState.State.DRAWING){

            gameRepository.drawingsList.add(game.value!!.currentDrawing)

            game.value = GameState(mutableListOf(), game.value?.turn!! + 1, GameState.State.WRITING)


        } else {

            gameRepository.wordsList.add(word)

            game.value = GameState(mutableListOf(), game.value?.turn!! + 1, GameState.State.DRAWING)
        }
        return this
    }
}