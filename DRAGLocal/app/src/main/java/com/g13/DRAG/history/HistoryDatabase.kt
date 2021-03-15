package com.g13.DRAG.history

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.g13.DRAG.local.Line
import com.g13.DRAG.local.Point
import java.util.*


/**
 * Specifies the contract to be supported by the History DB to be automatically generated by
 * Room build tools.
 *
 * Drawing to string and string to drawing methods have working code but the integration in the app
 * has flaws. To keep the app bug free, this functionality was withdrawn and the local high scores
 * just display date, first word and score
 */
@Database(entities = [GameResult::class], version = 1)
@TypeConverters(Converters::class)
abstract class HistoryDatabase : RoomDatabase() {
    /**
     * Gets the [GameResult] entity's DAO
     */
    abstract fun getGameResultsDao(): GameResultDao
}

/**
 * Contains functions for converting between in memory data types and their DB counterparts.
 *
 * IMPORTANT NOTICE: The architecture of the app does not support saving games with more than one round,
 * since the drawing list and word list are reset at the end of every round. The type converters here are unfinished
 * and aren't actually necessary for the history of the games, since those only display the first word, the score and the date
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date) = date.time

    @TypeConverter
    fun wordListToString(pattern: List<String>): String =
        pattern.fold(StringBuilder()) { acc, word ->
            acc.append("${word},")
        }.toString().dropLast(1) //remove the last "," after the last word

    @TypeConverter
    fun wordListFromString(stringPattern: String): List<String> =
        stringPattern.split(',')
            .filter { it.isNotBlank() }


    @TypeConverter
    fun drawingsToString(drawingList: List<List<Line>>): String {
        drawingList.fold(StringBuilder()) { acc, draw ->
            acc.append(draw.fold(StringBuilder()) { acc1, line ->
                acc1.append(line.points.fold(StringBuilder()) { acc2, (x, y) ->
                    acc2.append("${x},${y};")
                }.toString())
                acc1.append("|") //separa linhas do desenho
            }.toString())
            acc.append("!?") //separa desenhos de jogadores na string
        }.toString()

        return ""
    }

    @TypeConverter
    fun drawingsFromString(stringPattern: String): List<List<Line>> {

        var currentLine: MutableList<Point>
        var currentDrawing: MutableList<Line>
        var drawingList = mutableListOf<MutableList<Line>>()

        var drawingsString =
            stringPattern
                .split("!?")
                .filter{ it.isNotBlank() }

        for(drawing in drawingsString){

            currentLine = mutableListOf()
            currentDrawing = mutableListOf()
            drawingList = mutableListOf()

            val linesInDrawing = drawing.split("|")

            for(line in linesInDrawing){

                val points = line.split(";")

                for(point in points){
                    val coordenates = point.split(",")// [xcoord, ycoord]

                    if(coordenates[0].isNotBlank() && coordenates[1].isNotBlank())
                        currentLine.add(Point(coordenates[0].toFloat(), coordenates[1].toFloat()))
                }

                val tmp = Line(currentLine)

                currentDrawing.add(tmp)
            }

            drawingList.add(currentDrawing)
        }

        return mutableListOf()
    }
}