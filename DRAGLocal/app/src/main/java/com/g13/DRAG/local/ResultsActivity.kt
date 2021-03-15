package com.g13.DRAG.local

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.g13.DRAG.R
import com.g13.DRAG.history.HistoryActivity
import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivity : AppCompatActivity() {

    private val app by lazy { application as GameApplication }

    private val gameRepository by lazy{app.gameRepository}

    private val viewModel: ResultsViewModel by viewModels()

    private val ApiViewModel: APIViewModel by viewModels()

    private fun showResults(){

        val dialogBuilder = AlertDialog.Builder(this)

        val proceed: String = getString(R.string.proceed)

        var dialogString = viewModel.checkPoints(getString(R.string.gotPoints),
                getString(R.string.hasntGotPoints))
                .replace("x", gameRepository.player1Points.toString())

        if(gameRepository.currentRound.toString() == gameRepository.totalRounds){//end game

            //saves the game as soon as the activity knows the game has ended so the player
            //can see the current match in the match history
            viewModel.saveGame()

            nextRoundButton.text = getString(R.string.NewGame)

            nextRoundButton.setOnClickListener{

                dialogBuilder.setMessage(dialogString)
                    .setPositiveButton(proceed) { _, _ ->

                        gameRepository.clear()

                        if(ApiViewModel.state.value == APIViewModel.State.COMPLETE)
                            startActivity(Intent(this, MainActivity::class.java))
                        else
                            Toast.makeText(this,getString(R.string.fetch), Toast.LENGTH_LONG).show()

                    }.show()
            }

        }else{ //new round

            nextRoundButton.setOnClickListener{

                dialogBuilder.setMessage(dialogString)
                    .setPositiveButton(proceed) { _, _ ->

                        gameRepository.newRound()

                        if(ApiViewModel.state.value == APIViewModel.State.COMPLETE) {
                            startActivity(Intent(this, PlayActivity::class.java))
                        }else{
                            Toast.makeText(this,getString(R.string.fetch), Toast.LENGTH_LONG).show()
                        }

                    }.show()
            }
        }

        listButtons.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

                val intent = Intent(this, ShowResultsActivity::class.java)

                intent.putExtra("Id", position)

                startActivity(intent)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)

        val listItems: ArrayList<String?> = ArrayList()

        for (round in 0 until gameRepository.totalPlayers.toInt()) //props à aluna 43868
            listItems.add(getString(R.string.turnTextResult).replace("x", (round + 1).toString()))

        val adapter: ArrayAdapter<String?> =
                ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, listItems)

        listButtons.adapter = adapter

        //Prepare the view model for an API request
        ApiViewModel.setToIdle()

        //Observer for the new Round
        ApiViewModel.cache.observe(this){
            if (it != null) {

                ApiViewModel.addWordsToRepoCache(it)

                //repeat the method call bc if the cache is not empty it returns a word from the list (and by this point the cache has new words in case it was empty)
                ApiViewModel.getFirstWord()

                //gameRepository.firstWord = getWord(gameRepository.cache)

                ApiViewModel.setToComplete()
            }
        }

        //end game
        viewModel.publishedScore.observe(this){

            Log.v("debugMessage", "error:${it.error}; result: ${it.result}")

            Toast.makeText(this,getString(R.string.saveGame), Toast.LENGTH_LONG).show()
        }

        //Fetches a word from cache or sets in motion API call
        ApiViewModel.getFirstWord()

        showResults()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_history, menu)

        menu.findItem(R.id.historyMenuItem).setOnMenuItemClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            true
        }
        menu.findItem(R.id.highScoreMenuItem).setOnMenuItemClickListener {
            Toast.makeText(this, "The high score is ${gameRepository.highScore}", Toast.LENGTH_LONG).show()
            true
        }
        return true
    }
}
