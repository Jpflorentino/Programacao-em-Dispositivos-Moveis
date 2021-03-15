package com.g13.DRAG.HighScores.list

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g13.DRAG.local.GameApplication
import com.g13.DRAG.HighScores.HighScoreInfo

/**
 * The View Model used in the [ChallengesListActivity].
 *
 * Challenges are created by participants and are posted to the server, awaiting acceptance.
 */
class HighScoresListViewModel(app: Application) : AndroidViewModel(app) {

    private val app = getApplication<GameApplication>()

    /**
     * Contains the last fetched challenge list
     */
    val highScores: LiveData<List<HighScoreInfo>> = MutableLiveData()

    /**
     * Gets the high scores list by fetching them from the server. The operation's result is exposed
     * through [challenges]
     */
    fun fetchHighScores() {
        app.highscoresRepository.fetchScores(
            onSuccess = {
                (highScores as MutableLiveData<List<HighScoreInfo>>).value = it
            },
            onError = {
                Toast.makeText(app, "error getting list", Toast.LENGTH_LONG).show()
            }
        )
    }
}