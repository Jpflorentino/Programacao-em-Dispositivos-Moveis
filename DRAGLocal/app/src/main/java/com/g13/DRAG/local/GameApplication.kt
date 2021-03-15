package com.g13.DRAG.local

import android.app.Application
import androidx.room.Room
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.g13.DRAG.API.WordRepository
import com.g13.DRAG.history.HistoryDatabase
import com.g13.DRAG.HighScores.HighScoresRepository
import com.g13.DRAG.distribuido.DistributedRepository

private const val GLOBAL_PREFS = "GlobalPreferences"

const val TAG = "DRAGDebug"

class GameApplication : Application(){

    val gameRepository by lazy {
        GameRepository(
            getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE),
            //podemos mudar para um base de dados guardade de modo persistente usando o metodo databaseBuilder com o nome que damos a DB
            //Room.inMemoryDatabaseBuilder(this, HistoryDatabase::class.java).build()
            Room.databaseBuilder(this, HistoryDatabase::class.java, "HistoryDB").build()
        )
    }

    val wordRepository by lazy {
        WordRepository(
            getSharedPreferences(GLOBAL_PREFS, MODE_PRIVATE),
            Volley.newRequestQueue(this),
            jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        )
    }

    val highscoresRepository by lazy {
        HighScoresRepository(
            jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        )
    }

    val distributedRepository by lazy {
        DistributedRepository(
            jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        )
    }

}