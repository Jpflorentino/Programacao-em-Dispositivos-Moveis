package com.g13.DRAG.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.g13.DRAG.local.Line
import java.util.*

/*
It helps the ORM map the object into a database table
 */

@Entity(tableName = "games")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val players: Int,
    val rounds: Int,
    val firstWord: String,
    val words: List<String>,
    val drawings: List<List<Line>>,
    val date:Date = Date(),
    val score: Int
)