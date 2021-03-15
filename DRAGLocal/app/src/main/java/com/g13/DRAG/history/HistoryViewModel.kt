package com.g13.DRAG.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.g13.DRAG.local.GameApplication
import com.g13.DRAG.local.Line

/**
 * View model fot the activity that displays the game's score history.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Asynchronously gets all the existing scores.
     *
     * Implementation note: This eager approach (getting all the results) is only acceptable for
     * demonstration purposes.
     *
     * Implementation note: BE MINDFUL THAT for a given instance of the view model, results are only
     * fetched once from the DB. This approach serves us well in our current user task configuration,
     * but it may be cumbersome in other user experiences.
     */
    val results: LiveData<List<GameResult>> by lazy {
        getApplication<GameApplication>().gameRepository.getAllScores()
    }

    val nResults: LiveData<List<GameResult>> by lazy{
        getApplication<GameApplication>().gameRepository.getNScores(5)
    }

    fun saveLoadedGamesIntoGameRepository(loadedGames: List<GameResult>){
        getApplication<GameApplication>().gameRepository.loadedGames = loadedGames
    }
}