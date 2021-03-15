package com.g13.DRAG.local

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.g13.DRAG.R
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    var timer : Long = 60000

    private val app by lazy { application as GameApplication }

    private val gameRepository by lazy{app.gameRepository}

    private val viewModel : PlayActivityViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    fun drawingFunction() {

        guessWordEditText.isEnabled = false

        playView.isEnabled = true

        guessWordEditText.setText(gameRepository.wordsList[gameRepository.wordsList.lastIndex])

        lateinit var firstPoint: Point

        var dragPoint: Point

        playView.model = viewModel.game.value?.currentDrawing?.let { PlayModel(it) } //either blank or with the current drawing

        playView.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    firstPoint = Point(event.x, event.y)

                    viewModel.stroke = mutableListOf()

                    viewModel.stroke.add(firstPoint)

                    viewModel.line = Line(viewModel.stroke)
                }
                MotionEvent.ACTION_MOVE -> {

                    dragPoint = Point(event.x, event.y) //muda quando se arrasta o dedo na view

                    viewModel.stroke.add(dragPoint)

                    viewModel.game.value?.currentDrawing?.add(viewModel.line)

                    playView.model = viewModel.game.value?.currentDrawing?.let { PlayModel(it) }

                }
            }
            true
        }
    }

    private fun writingFunction(){
        guessWordEditText.isEnabled = true

        playView.isEnabled = false

        guessWordEditText.setText("")

        if(gameRepository.drawingsList.size != 0)
            playView.model = PlayModel(gameRepository.drawingsList[gameRepository.drawingsList.lastIndex])

    }

    private fun changePlayer(){

        viewModel.changeState(guessWordEditText.text.toString())

        if(viewModel.game.value?.turn.toString() > gameRepository.totalPlayers){

            //this intent is pointless, it's here so we can send an intent with the index fo the database game and this assures consistency
            val intent = Intent(this, ResultsActivity::class.java)

            startActivity(intent)

        }

        turnTextView.text =
            getString(R.string.turnText)
                .replace("x", viewModel.game.value?.turn.toString())
                .replace("y", gameRepository.totalPlayers)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        turnTextView.text =
            getString(R.string.turnText)
                .replace("x", viewModel.game.value?.turn.toString())
                .replace("y", gameRepository.totalPlayers)

        roundTextView.text = "${gameRepository.currentRound}"

        viewModel.game.observe(this) {

            viewModel.scheduleTransition(timer)

            when (viewModel.game.value?.state) {
                GameState.State.DRAWING ->{
                    drawingFunction()
                }
                GameState.State.WRITING ->{
                    writingFunction()
                }
            }
        }

        viewModel.scheduleComplete.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                changePlayer()
            }
        }
    }
}

