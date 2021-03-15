package com.g13.DRAG.distribuido

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChallengeInfo(
    val id: String,
    val challengerName: String,
    val challengerMessage: String,
    val totalRounds: String,
    var playersEnrolled: String,
    val isReady: Boolean,
    val firstWord: String,
    val totalPlayers: String
) : Parcelable