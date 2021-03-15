package com.g13.DRAG.local

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.g13.DRAG.R
import com.g13.DRAG.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModels()

    private val APIViewModel : APIViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //gameRepository.changeHighScore(0) //for firestore high scores debugging purposes

        playButton.setOnClickListener{

            viewModel.setBasicParameters(
                playersEditText.text.toString(),
                roundsEditText.text.toString(),
                playerNameEditText.text.toString()
            )

            val intent = Intent(this, PlayActivity::class.java)

            val parameters: String = getString(R.string.InvalidParameters)

            //minimum 5 players and 1 rounds
            if( viewModel.playerNameVM.isEmpty() || viewModel.totalPlayersVM.isEmpty() || viewModel.totalRoundsVM.isEmpty() || viewModel.firstWord.isEmpty() || Integer.parseInt(viewModel.totalRoundsVM) < 1 || Integer.parseInt(viewModel.totalPlayersVM) < 5 ){ //it has to be this order, otherwise it checks if null < 5
                Toast.makeText(this, parameters, Toast.LENGTH_SHORT).show()
            }else{

                if(Integer.parseInt(viewModel.totalPlayersVM) % 2 != 0){ //nr de jogadores impar, o dono do bloco passa ao jogador seguinte

                    val dialogBuilder = AlertDialog.Builder(this)

                    val alert: String = getString(R.string.alertMessage)
                    val proceed: String = getString(R.string.proceed)

                    dialogBuilder.setMessage(alert)
                        // positive button text and action
                        .setPositiveButton(proceed) { _, _ ->
                            startActivity(intent)
                        }.show()
                }else{
                    startActivity(intent)
                }
            }
        }
    }
}
