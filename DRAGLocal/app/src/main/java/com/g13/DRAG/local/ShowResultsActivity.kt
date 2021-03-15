package com.g13.DRAG.local

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.g13.DRAG.R
import kotlinx.android.synthetic.main.activity_play.*

class ShowResultsActivity : AppCompatActivity() {

    private val app by lazy { application as GameApplication }

    private val gameRepository by lazy{app.gameRepository}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val index = intent.extras?.get("Id").toString().toInt()

        var drawCount = -1 //index of the drawings in the round drawings array

        var wordCount = 0 //index of the drawings in the round words array

        lateinit var word: String

        //var wordCount = index

        /*
        Index | Drawing Index | Word Index
        0           0               0
        1           0               1
        2           1               1
        3           1               2
        4           2               2
        5           2               3
        6           3               3
         */

        if (index != 0) { //if index is 0 the both counts are zero

            for (i in 0..index) { //the for loop to increment the counts based on the index

                if (i % 2 == 0) { //the draw counter only increments in the even rounds; the draw count starts at -1 pq 0 is even
                    drawCount += 1

                } else { //the word counter only increments in the uneven rounds

                    wordCount += 1
                }
            }

        }else{
            drawCount = 0
        }

        word = if(gameRepository.wordsList[wordCount].isEmpty()){

            getString(R.string.NoInput)

        }else{
            gameRepository.wordsList[wordCount]
        }

        val draw = gameRepository.drawingsList[drawCount]

        playView.model = PlayModel(draw)

        guessWordEditText.setText(word)

        turnTextView.text =
            getString(R.string.turnText)
                .replace("x", (index + 1).toString())
                .replace("y", gameRepository.totalPlayers)

        roundTextView.text = gameRepository.currentRound.toString()

        playView.isEnabled = false

        guessWordEditText.isEnabled = false
    }
}