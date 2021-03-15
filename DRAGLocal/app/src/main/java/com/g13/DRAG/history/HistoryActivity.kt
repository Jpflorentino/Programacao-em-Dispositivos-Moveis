package com.g13.DRAG.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g13.DRAG.R
import com.g13.DRAG.local.ResultsActivity
import com.g13.DRAG.local.ShowResultsActivity

/**
 * The recyclerview´s view holder
 */
class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val topText: TextView = itemView.findViewById(R.id.historyItemTopText)
    val bottomText: TextView = itemView.findViewById(R.id.historyItemBottomText)
}

/**
 * The recyclerview´s adapter
 */
class HistoryAdapter(
        private val ctx: Context,
        private val history: List<GameResult>
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val topFormatString = ctx.resources.getString(R.string.history_top_text)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    /*
    texto de cima: firstWord e Score guardado
    texto de baixo: data do jogo
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.topText.text = topFormatString
                        .replace("word", history[position].words[0])
                        .replace("score",history[position].score.toString() )

        holder.bottomText.text = history[position].date.toString()

        /*holder.itemView.setOnClickListener {
            //Envia-se um intent à atividade que mostra os resultados de modo a se saber em que indice está o jogo que queremos
            val context = holder.itemView.context
            val intent = Intent(context, ResultsActivity::class.java).putExtra("history", position)
            context.startActivity(intent)
        }*/
    }

    override fun getItemCount() = history.size
}

    /**
     * Screen used to display the game scores' history using a Recycler View.
     */
    class HistoryActivity : AppCompatActivity() {

        val viewModel: HistoryViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_history)

            val history = findViewById<RecyclerView>(R.id.historyView)

            history.setHasFixedSize(true)

            history.layoutManager = LinearLayoutManager(this)

            viewModel.nResults.observe(this) {
                history.adapter = HistoryAdapter(this, it)

                //Save the games into the "loadedGames" so we can access the data from the other activities
                //viewModel.saveLoadedGamesIntoGameRepository(it)
            }
        }
    }

