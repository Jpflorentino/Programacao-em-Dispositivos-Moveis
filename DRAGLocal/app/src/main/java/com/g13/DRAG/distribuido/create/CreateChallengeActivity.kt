package com.g13.DRAG.distribuido.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.g13.DRAG.HighScores.Result
import com.g13.DRAG.HighScores.State
import com.g13.DRAG.R
import com.g13.DRAG.databinding.ActivityCreateChallengeBinding
import com.g13.DRAG.distribuido.ChallengeInfo
import com.g13.DRAG.distribuido.game.GameActivity
import com.g13.DRAG.local.APIViewModel

const val RESULT_EXTRA = "CCA.Result"

class CreateChallengeActivity : AppCompatActivity() {

    /**
     * The associated view model instance
     */
    private val viewModel: CreateChallengeViewModel by viewModels()

    private val APIViewModel: APIViewModel by viewModels()

    /**
     * The associated View Binding
     */
    private val binding: ActivityCreateChallengeBinding by lazy {
        ActivityCreateChallengeBinding.inflate(layoutInflater)
    }

    /**
     * Callback method that handles the activity initiation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //sets an bserver on the cache just in case it's empty and it needs to be fetched
        APIViewModel.cache.observe(this){ wordList ->
            if (wordList != null) {

                APIViewModel.addWordsToRepoCache(wordList)

                APIViewModel.getFirstWord()
            }
        }

        //Fetches a word from cache or sets in motion API call
        APIViewModel.getFirstWord()

        viewModel.result.observe(this) {
            if (it.state == State.COMPLETE) {
                if (it.result != null) {
                    setResult(Activity.RESULT_OK, Intent().putExtra(RESULT_EXTRA, it.result))

                    viewModel.listenForChallenge(it.result.id)

                    Toast.makeText(this, R.string.game_screen_title_distributed_challenger_waiting, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, R.string.error_creating_challenge, Toast.LENGTH_LONG).show()
                }
            }
        }

        /*
        After creating the challenge the creator is waiting for the other players to join
        There is no activity for this they just stay at the create challenge screen
        It sets the creator as player one
         */
        viewModel.isChallengeReady.observe(this){

            var mutableResult = viewModel.result as MutableLiveData<Result<ChallengeInfo, Exception>>

            mutableResult.value?.result?.playersEnrolled = (mutableResult.value?.result?.playersEnrolled?.toInt()?.plus(1)).toString()

            if(it){
                startActivity(Intent(this, GameActivity::class.java).apply {
                    putExtra(ACCEPTED_CHALLENGE_EXTRA, mutableResult.value?.result)
                    putExtra(LOCAL_PLAYER_EXTRA, 1)
                })
            }
        }

        binding.create.setOnClickListener {
            if(
                binding.name.text.isEmpty() ||
                binding.message.text.isEmpty() ||
                binding.totalPlayers?.text.toString().isEmpty() ||
                binding.totalRounds.text.isEmpty() ||
                binding.totalPlayers?.text.toString().toInt() < 5 ||
                binding.totalRounds?.text.toString().toInt() < 0){

                Toast.makeText(this, getString(R.string.parameterError), Toast.LENGTH_LONG).show()
            } else{
                viewModel.createChallenge(
                    binding.name.text.toString(),
                    binding.message.text.toString(),
                    binding.totalRounds.text.toString(),
                    "1",
                    false,
                    APIViewModel.firstWord,
                    binding.totalPlayers?.text.toString()
                )
            }
        }
    }
}