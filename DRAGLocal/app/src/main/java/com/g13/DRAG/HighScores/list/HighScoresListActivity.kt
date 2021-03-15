package com.g13.DRAG.HighScores.list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.g13.DRAG.R
import com.g13.DRAG.databinding.ActivityHighscoresListBinding
import com.g13.DRAG.HighScores.HighScoreInfo
import com.g13.DRAG.HighScores.list.list.HighScoreListAdapter
import kotlinx.android.synthetic.main.activity_highscores_list.*

/**
 * The activity used to display the list of existing challenges.
 */
class HighScoresListActivity : AppCompatActivity() {

    /**
     * Called whenever the challenges list is to be fetched again.
     */
    private fun updateScoresList() {
        refreshLayout.isRefreshing = true
        viewModel.fetchHighScores()
    }

    /**
     * Called whenever a list element is selected. The player that accepts the challenge is the
     * first to make a move.
     *
     * @param challenge the selected challenge
     *
     * This is not really necesary anymore but the adapter needs a item
     */
    private fun challengeSelected(challenge: HighScoreInfo) {
        AlertDialog.Builder(this)
            .setTitle("${getString(R.string.playerName)}: ${challenge.playerName}")
            .setMessage("${getString(R.string.saveGame)}: ${challenge.highScore}" + "\n" + "${getString(R.string.firstWord)}: ${challenge.firstWord}")
            .setPositiveButton("ok", null)
            .create()
            .show()
    }

    /**
     * The associated view model instance
     */
    private val viewModel: HighScoresListViewModel by viewModels()

    /**
     * The associated view binding
     */
    private val binding: ActivityHighscoresListBinding by lazy {
        ActivityHighscoresListBinding.inflate(layoutInflater)
    }

    /**
     * Callback method that handles the activity initiation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        scoresList.setHasFixedSize(true)
        scoresList.layoutManager = LinearLayoutManager(this)

        // Get view model instance and add its contents to the recycler view
        viewModel.highScores.observe(this) {
            scoresList.adapter = HighScoreListAdapter(it, ::challengeSelected)
            refreshLayout.isRefreshing = false
        }

        // Setup ui event handlers
       refreshLayout.setOnRefreshListener {
            updateScoresList()
        }
    }

    override fun onStart() {
        super.onStart()
        updateScoresList()
    }
}