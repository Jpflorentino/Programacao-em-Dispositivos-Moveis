package com.g13.DRAG.local

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository by lazy{getApplication<GameApplication>().gameRepository}

    var playerNameVM = ""
    var totalPlayersVM = ""
    var totalRoundsVM: String = ""
    var firstWord = gameRepository.firstWord

    fun setBasicParameters(totalPlayers: String, totalRounds: String, playerName: String){

        playerNameVM = playerName
        totalPlayersVM = totalPlayers
        totalRoundsVM = totalRounds

        //it's redundant to do this because there is a firstWord property but it makes the showing of the results easier
        gameRepository.wordsList.add(firstWord)

        gameRepository.setBasicParameters(
            totalPlayers,
            totalRounds,
            playerName
        )
    }
}
