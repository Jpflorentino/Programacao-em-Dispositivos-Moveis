package com.g13.DRAG.local

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.g13.DRAG.R
import com.g13.DRAG.HighScores.list.HighScoresListActivity
import com.g13.DRAG.distribuido.list.ChallengesListActivity
import kotlinx.android.synthetic.main.activity_choosemode.*


class ModeChoiceActivity : AppCompatActivity(){

    private val viewModel : APIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choosemode)

        //this is done regardless of game choice:
        //sets an bserver on the cache just in case it's empty and it needs to be fetched
       viewModel.cache.observe(this){ wordList ->
            if (wordList != null) {

                viewModel.addWordsToRepoCache(wordList)

                viewModel.getFirstWord()
            }
        }

        //Fetches a word from cache or sets in motion API call
        viewModel.getFirstWord()

        //local button changes to main Activity for the version implemented in the first version
        localButton.setOnClickListener{

            if(viewModel.state.value == APIViewModel.State.COMPLETE) {

                viewModel.setToIdle()

                startActivity(Intent(this, MainActivity::class.java))
            }else{
                Toast.makeText(this, R.string.fetch, Toast.LENGTH_LONG).show()
            }
        }

        highScoresButton.setOnClickListener{
                startActivity(Intent(this, HighScoresListActivity::class.java))
        }

        onlineButton.setOnClickListener{
            if(viewModel.state.value == APIViewModel.State.COMPLETE) {

                viewModel.setToIdle()

                startActivity(Intent(this, ChallengesListActivity::class.java))
            }else{
                Toast.makeText(this, R.string.fetch, Toast.LENGTH_LONG).show()
            }
        }
    }
}