package com.g13.DRAG.distribuido.list

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g13.DRAG.R
import com.g13.DRAG.local.GameApplication
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import com.g13.DRAG.HighScores.Result
import com.g13.DRAG.HighScores.State
import com.g13.DRAG.distribuido.*
import com.g13.DRAG.local.TAG
import com.google.firebase.firestore.DocumentSnapshot

private fun DocumentSnapshot.toChallengeInfo()=
    ChallengeInfo(
        id,
        data?.get(CHALLENGER_NAME) as String,
        data!![CHALLENGER_MESSAGE] as String,
        data!![CHALLENGER_TOTALROUNDS] as String,
        data!![CHALLENGER_PLAYERS] as String,
        data!![CHALLENGER_READY] as Boolean,
        data!![CHALLENGER_FW] as String,
        data!![CHALLENGER_TOTALPLAYERS] as String
    )

class ChallengesListViewModel(app: Application) : AndroidViewModel(app) {

    private val app = getApplication<GameApplication>()

    /**
     * Contains the last fetched challenge list
     */
    val challenges: LiveData<List<ChallengeInfo>> = MutableLiveData()

    /**
     * Gets the challenges list by fetching them from the server. The operation's result is exposed
     * through [challenges]
     */
    fun fetchChallenges() {
        app.distributedRepository.fetchChallenges(
            onSuccess = {
                (challenges as MutableLiveData<List<ChallengeInfo>>).value = it
            },
            onError = {
                Toast.makeText(app, R.string.error_getting_list, Toast.LENGTH_LONG).show()
            }
        )
    }

    /**
     * Contains information about the enrolment in a challenge.
     */
    val enrolmentResult: LiveData<Result<ChallengeInfo, Exception>> = MutableLiveData()

    //live data para saber se o challenge está ready
    val isChallengeReady: MutableLiveData<ChallengeInfo> = MutableLiveData()

    fun listenForChallenge(
        challengeInfo: ChallengeInfo,
    ): LiveData<ChallengeInfo> {

        val docRef = Firebase.firestore
            .collection("challenges")
            .document(challengeInfo.id)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if(snapshot != null && snapshot?.getBoolean("isReady") == true)
                isChallengeReady.value = snapshot.toChallengeInfo()
        }

        return isChallengeReady
    }


    /**
     * Tries to accepts the given challenge. The result of the asynchronous operation is exposed
     * through [enrolmentResult]
     */
    fun tryAcceptChallenge(challengeInfo: ChallengeInfo) {

        listenForChallenge(challengeInfo)

        val state = enrolmentResult as MutableLiveData<Result<ChallengeInfo, Exception>>

        state.value = Result(State.ONGOING, challengeInfo)

        //update the "isReady" property or the enrolled Players depending on how man ypeople accepted
        state.value!!.result?.let {
            app.distributedRepository.updateChallenge(
                it,
                { state.value = Result(State.COMPLETE, it)},
                {error -> state.value = Result(State.COMPLETE, challengeInfo, error) })
        }
    }

    /**
     * Resets the state of the enrolment
     */
    fun resetEnrolmentResult() {
        (enrolmentResult as MutableLiveData<Result<ChallengeInfo, Exception>>).value = Result()
    }
}
