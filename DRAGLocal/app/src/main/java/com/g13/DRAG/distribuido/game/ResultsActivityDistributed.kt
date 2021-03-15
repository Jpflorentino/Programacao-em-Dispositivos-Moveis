package com.g13.DRAG.distribuido.game

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.g13.DRAG.R
import com.g13.DRAG.distribuido.ChallengeInfo
import com.g13.DRAG.distribuido.list.ChallengesListActivity
import com.g13.DRAG.distribuido.list.ChallengesListViewModel
import com.g13.DRAG.distribuido.list.view.ChallengeViewHolder
import com.g13.DRAG.local.GameApplication
import com.g13.DRAG.local.ModeChoiceActivity
import com.g13.DRAG.local.TAG
import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivityDistributedViewModel(
    app: Application,
) : AndroidViewModel(app) {

    fun deleteGame(challengeId: String){
        getApplication<GameApplication>().distributedRepository.deleteGame(challengeId,
            { Log.v(TAG, "published game sucessfully")},
            {Log.v(TAG, "unpublished game sucessfully")})
    }
}

class ResultsActivityDistributed() : AppCompatActivity() {

    private val viewModel: ResultsActivityDistributedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_results)

        val totalPlayers: String = intent.extras?.get("totalPlayers").toString()

        val totalRounds: String = intent.extras?.get("totalRounds").toString()

        val challengeId: String = intent.extras?.get("challengeInfoID").toString()

        val listItems: ArrayList<String?> = ArrayList()

        for (round in 0 until totalPlayers.toInt())
            listItems.add(getString(R.string.turnTextResult).replace("x", (round + 1).toString()))

        val adapter: ArrayAdapter<String?> =
            ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, listItems)

        listButtons.adapter = adapter

        if(totalRounds == "1"){
            nextRoundButton.text= getString(R.string.NewGame)

            nextRoundButton.setOnClickListener {
                viewModel.deleteGame(challengeId)
                startActivity(Intent(this, ModeChoiceActivity::class.java))
            }

        }else{
            nextRoundButton.text= getString(R.string.next_round)
            nextRoundButton.setOnClickListener {
                startActivity(Intent(this, ChallengesListActivity::class.java))
            }
        }
    }
}