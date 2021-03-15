package com.g13.DRAG.history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/*
Data Access Object: Tipo de dados que vai realizar acessos a base de dados para ter a informação segundo a Entity que foi definida no GameResult
O codigo correspondente é criado no processo de build
 */

@Dao
interface GameResultDao {

    @Query("SELECT * FROM games ORDER BY date DESC")
    fun loadAllGames(): LiveData<List<GameResult>>

    @Query("SELECT * FROM games ORDER BY date DESC LIMIT :count")
    fun loadLastGames(count: Int): LiveData<List<GameResult>>

    @Insert
    fun insertGame(game: GameResult)
}