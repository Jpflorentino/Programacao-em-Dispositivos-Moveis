package com.g13.DRAG.HighScores

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HighScoreInfo(
    val id: String,
    val playerName: String,
    val highScore: String,
    val firstWord: String
) : Parcelable