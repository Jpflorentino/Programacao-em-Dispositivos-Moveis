package com.g13.DRAG.distribuido.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.g13.DRAG.R
import com.g13.DRAG.databinding.ActivityPlayBinding
import com.g13.DRAG.distribuido.ChallengeInfo
import com.g13.DRAG.distribuido.create.ACCEPTED_CHALLENGE_EXTRA
import com.g13.DRAG.distribuido.create.LOCAL_PLAYER_EXTRA
import com.g13.DRAG.local.GameState
import com.g13.DRAG.local.Line
import com.g13.DRAG.local.PlayModel
import com.g13.DRAG.local.Point
import kotlinx.android.synthetic.main.activity_play.*

class GameActivity: AppCompatActivity()  {

    var timer : Long = 60000

    private val localPlayer: Int by lazy {
        intent.extras?.getInt(LOCAL_PLAYER_EXTRA) ?:
        throw IllegalArgumentException("No local player")
    }

    private val challengeInfo: ChallengeInfo by lazy {
        intent.getParcelableExtra<ChallengeInfo>(ACCEPTED_CHALLENGE_EXTRA) ?:
        throw IllegalArgumentException("Mandatory extra $ACCEPTED_CHALLENGE_EXTRA not present")
    }

    private val viewModel: GameViewModel by viewModels {
        @Suppress("UNCHECKED_CAST")
        object: ViewModelProvider.Factory {
            override fun <VM : ViewModel?> create(modelClass: Class<VM>): VM {
                return GameViewModel(application, 1, challengeInfo) as VM
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun drawingFunction() {

        guessWordEditText.isEnabled = false

        playView.isEnabled = true

        guessWordEditText.setText(viewModel.wordList[viewModel.wordList.lastIndex])

        lateinit var firstPoint: Point

        var dragPoint: Point

        playView.model = viewModel.currentDrawing?.let { PlayModel(it) } //either blank or with the current drawing

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

                    viewModel.currentDrawing?.add(viewModel.line)

                    playView.model = viewModel.currentDrawing?.let { PlayModel(it) }

                }
            }
            true
        }
    }

    private fun writingFunction(){
        guessWordEditText.isEnabled = true

        playView.isEnabled = false

        guessWordEditText.setText("")

        viewModel.currentDrawing = playView.model?.lines as MutableList<Line>

    }

    private fun changePlayer(){

        viewModel.changeState(guessWordEditText.text.toString())

        //VAmos ignorar a regra de fazer o segundo jogador começar a desenhar quando o totalPlayers e impar de modo a entregar algo minimamente funcional
        //rondas pares -> escrita; rondas impares- > drawing
        //THIS IS NOT DOING ANYTHING AND WE DONT KNOW WHY
        if(localPlayer == viewModel.game.value?.turn){
            if(localPlayer % 2 == 0){
                binding.playView.isEnabled = false
                binding.guessWordEditText.isEnabled = true
            }else{
                binding.playView.isEnabled = true
                binding.guessWordEditText.isEnabled = false
            }
        }

        if(viewModel.game.value?.turn.toString() > viewModel.totalPlayers.toString()){

            val intent = Intent(this, ResultsActivityDistributed::class.java)
                .putExtra("totalPlayers", viewModel.challengeInfo.totalPlayers)
                .putExtra("totalRounds", viewModel.challengeInfo.totalRounds)
                .putExtra("challengeInfoID", challengeInfo.id)

            startActivity(intent)

        }

        turnTextView.text =
            getString(R.string.turnText)
                .replace("x", viewModel.game.value?.turn.toString())
                .replace("y", "${viewModel.totalPlayers}")
    }

    private val binding: ActivityPlayBinding by lazy {
        ActivityPlayBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.removeChallenge(challengeInfo.id)

        binding.playView.isEnabled = false
        binding.guessWordEditText.isEnabled = false


        turnTextView.text =
            getString(R.string.turnText)
                .replace("x", viewModel.game.value?.turn.toString())
                .replace("y", "${viewModel.totalPlayers}")

        roundTextView.text = "${viewModel.currentRound}"

        viewModel.gameState.observe(this) {

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
