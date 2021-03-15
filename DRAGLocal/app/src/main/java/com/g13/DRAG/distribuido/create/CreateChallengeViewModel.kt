package com.g13.DRAG.distribuido.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g13.DRAG.distribuido.ChallengeInfo
import com.g13.DRAG.local.GameApplication
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.g13.DRAG.HighScores.Result
import com.g13.DRAG.HighScores.State

const val ACCEPTED_CHALLENGE_EXTRA = "GameActivity.AcceptedChallengeExtra"
const val LOCAL_PLAYER_EXTRA = "GameActivity.LocalPlayerListenExtra"
const val TURN_PLAYER_EXTRA = "GameActivity.TurnPlayerListenExtra"

class CreateChallengeViewModel(app: Application) : AndroidViewModel(app) {

    val result: LiveData<Result<ChallengeInfo, Exception>> = MutableLiveData()

    //live data mediator para saber se o challenge está ready
    val isChallengeReady: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Creates a challenge with the given arguments. The result is placed in [result]
     */
    fun createChallenge(name: String, message: String, totalRounds : String, playersEnrolled : String, isReady: Boolean, firstWord : String, totalPlayers: String) {
        val app = getApplication<GameApplication>()

        val mutableResult = result as MutableLiveData<Result<ChallengeInfo, Exception>>

        app.distributedRepository.publishChallenge(name, message,totalRounds, isReady, playersEnrolled,firstWord,totalPlayers,
            onSuccess = {mutableResult.value = Result(State.COMPLETE, result = it) },
            onError = { mutableResult.value = Result(State.COMPLETE, error = it) }
        )
    }

    fun listenForChallenge(
        challengeId: String,
    ): LiveData<Boolean> {

        val docRef = Firebase.firestore
            .collection("challenges")
            .document(challengeId)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if(snapshot != null && snapshot?.getBoolean("isReady") == true){
                isChallengeReady.value = true
            }

        }

        return isChallengeReady
    }
}