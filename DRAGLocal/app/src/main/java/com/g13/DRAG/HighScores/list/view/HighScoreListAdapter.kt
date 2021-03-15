package com.g13.DRAG.HighScores.list.list

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.g13.DRAG.R
import com.g13.DRAG.HighScores.HighScoreInfo

/**
 * Represents views (actually, the corresponding holder) that display the information pertaining to
 * a [HighScoreInfo] instance
 */
class HighScoreViewHolder(private val view: ViewGroup) : RecyclerView.ViewHolder(view) {

    private val challengerNameView: TextView = view.findViewById(R.id.PlayerName)
    private val challengerMessageView: TextView = view.findViewById(R.id.HighScore)

    /**
     * Starts the item selection animation and calls [onAnimationEnd] once the animation ends
     */
    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(view.context, R.color.list_item_background),
            ContextCompat.getColor(view.context, R.color.list_item_background_selected),
            ContextCompat.getColor(view.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = view.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.start()

        animation.doOnEnd { onAnimationEnd() }
    }

    /**
     * Used to create an association between the current view holder instance and the given
     * data item
     *
     * @param   challenge               the challenge data item
     * @param   itemSelectedListener    the function to be called whenever the item is selected
     */
    fun bindTo(challenge: HighScoreInfo?, itemSelectedListener: (HighScoreInfo) -> Unit) {
        challengerNameView.text = challenge?.playerName ?: ""
        challengerMessageView.text = challenge?.highScore ?: ""

        if (challenge != null)
            view.setOnClickListener {
                startAnimation {
                    itemSelectedListener(challenge)
                }
            }
    }
}

/**
 * Adapts the view model instances to be displayed in a [RecyclerView]
 */
class HighScoreListAdapter(
    private val contents: List<HighScoreInfo>,
    private val itemSelectedListener: (HighScoreInfo) -> Unit) :
    RecyclerView.Adapter<HighScoreViewHolder>() {

    override fun onBindViewHolder(holder: HighScoreViewHolder, position: Int) {
        holder.bindTo(contents[position], itemSelectedListener)
    }

    override fun getItemCount(): Int = contents.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false) as ViewGroup

        return HighScoreViewHolder(view)
    }
}