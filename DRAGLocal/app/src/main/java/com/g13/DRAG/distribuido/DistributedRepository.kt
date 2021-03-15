package com.g13.DRAG.distribuido

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.g13.DRAG.HighScores.Result
import com.g13.DRAG.HighScores.State
import com.g13.DRAG.distribuido.game.Game
import com.g13.DRAG.distribuido.game.GameDTO
import com.g13.DRAG.distribuido.game.toGame
import com.g13.DRAG.local.Line
import com.g13.DRAG.local.TAG
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val GAMES_COLLECTION = "games"
private const val CHALLENGES_COLLECTION = "challenges"

const val CHALLENGER_NAME = "challengerName"
const val CHALLENGER_MESSAGE = "challengerMessage"
const val CHALLENGER_TOTALROUNDS = "challengerTotalRounds"
const val CHALLENGER_PLAYERS = "challengerPlayersEnrolled"
const val CHALLENGER_READY = "isReady"
const val CHALLENGER_FW = "firstWord"
const val CHALLENGER_TOTALPLAYERS = "totalPlyers"

private const val GAME_STATE_KEY = "game"
private const val CHALLENGE_INFO_KEY = "challenge"


/**
 * Extension function used to convert createdChallenge documents stored in the Firestore DB into
 * [ChallengeInfo] instances
 */
private fun QueryDocumentSnapshot.toChallengeInfo() =
    ChallengeInfo(
        id,
        data[CHALLENGER_NAME] as String,
        data[CHALLENGER_MESSAGE] as String,
        data[CHALLENGER_TOTALROUNDS] as String,
        data[CHALLENGER_PLAYERS] as String,
        data[CHALLENGER_READY] as Boolean,
        data[CHALLENGER_FW] as String,
        data[CHALLENGER_TOTALPLAYERS] as String
    )

/**
 * The repository for the distributed DRAG
 */
class DistributedRepository(private val mapper: ObjectMapper) {

    /**
     * Fetches the list of open challenges from the backend
     *
     * Implementation note: This fetches ALL open challenges. In realistic scenarios this is
     * a poor design decision because the resulting data set size is unbounded!
     */
    fun fetchChallenges(onSuccess: (List<ChallengeInfo>) -> Unit, onError: (Exception) -> Unit) {
        Firebase.firestore.collection(CHALLENGES_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                Log.v(TAG, "Repo got list from Firestore")
                onSuccess(result.map { it.toChallengeInfo() }.toList())
            }
            .addOnFailureListener {
                Log.e(TAG, "Repo: An error occurred while fetching list from Firestore")
                Log.e(TAG, "Error was $it")
                onError(it)
            }
    }

    /**
     * Unpublishes the challenge with the given identifier.
     */
    fun unpublishChallenge(challengeId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    /**
     * Publishes a challenge with the given [name] and [message].
     */
    fun publishChallenge(
        name: String,
        message: String,
        totalRounds: String,
        isReady: Boolean,
        playersEnrolled: String = "1",
        firstWord:String,
        totalPlayers: String,
        onSuccess: (ChallengeInfo) -> Unit,
        onError: (Exception) -> Unit) {

        Firebase.firestore.collection(CHALLENGES_COLLECTION)
            .add(hashMapOf(CHALLENGER_NAME to name, CHALLENGER_MESSAGE to message, CHALLENGER_TOTALROUNDS to totalRounds, CHALLENGER_PLAYERS to playersEnrolled, CHALLENGER_READY to isReady, CHALLENGER_FW to firstWord, CHALLENGER_TOTALPLAYERS to totalPlayers))
            .addOnSuccessListener { onSuccess(ChallengeInfo(it.id, name, message,totalRounds, playersEnrolled, isReady, firstWord, totalPlayers)) }
            .addOnFailureListener { onError(it) }
    }

    /*
    update the "isReady" field to start the game
     */
    fun updateReady(challengeId: String){
        Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeId)
            .update(CHALLENGER_READY, true)
    }

    /*
    Updates the challenge document with the new total of players who accepted it
    If the total players amount is reached when accepting the challenge it sets the property "isReady" to true, starting the game
     */
    fun updateChallenge(challengeInfo: ChallengeInfo ,onSuccess: () -> Unit, onError: (Exception) -> Unit){

        var updateString : String

        var documentCenas = Firebase.firestore
            .collection(CHALLENGES_COLLECTION)
            .document(challengeInfo.id)

        Firebase.firestore.runTransaction {
            val snapshot = it.get(documentCenas)

            var currentPlayers = snapshot.data?.get(CHALLENGER_PLAYERS).toString() //correct String with the number of enrolled players

            Log.i("testeUpdateChallenge", "challengeInfo: ${challengeInfo.playersEnrolled} snapshot: ${snapshot["totalPlyers"].toString()}")

            if (challengeInfo.playersEnrolled == snapshot["totalPlyers"].toString().toInt().minus(1).toString()){ //when the last player joins, start the game

               updateReady(challengeInfo.id)

            }else if(challengeInfo.playersEnrolled.toInt() < snapshot["totalPlyers"].toString().toInt().minus(1)){ //increase the number on enrolled players in the document

                updateString = currentPlayers.toInt().plus(1).toString()

                it.update(documentCenas, CHALLENGER_PLAYERS, updateString)
            }

        }
            .addOnSuccessListener { Log.d(TAG, "Transaction success!") }
            .addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }
    }

    /*
    Delete game from the games collection
     */
    fun deleteGame(
        challengeId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challengeId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    /**
     * Updates the shared game state
    **/
    fun updateGameState(
        game: Game,
        challenge: ChallengeInfo,
        onSuccess: (Game) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val gameStateBlob = mapper.writeValueAsString(game.toGameDTO())
        val challengeBlob = mapper.writeValueAsString(challenge)

        Firebase.firestore.collection(GAMES_COLLECTION)
            .document(challenge.id)
            .set(hashMapOf(
                GAME_STATE_KEY to gameStateBlob,
                CHALLENGE_INFO_KEY to challengeBlob
            ))
            .addOnSuccessListener { onSuccess(game) }
            .addOnFailureListener { onError(it) }
    }

    /**
     * Subscribes for changes in the game with the given identifier (i.e. [challengeId])
     *
     **/
    fun subscribeTo(
        challengeId: String,
        onSubscriptionError: (Exception) -> Unit,
        onStateChanged: (Game) -> Unit
    ): ListenerRegistration {

        return Firebase.firestore
            .collection(GAMES_COLLECTION)
            .document(challengeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onSubscriptionError(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {
                    val gameDTO = mapper.readValue(
                        snapshot.get(GAME_STATE_KEY) as String,
                        GameDTO::class.java
                    )
                    onStateChanged(gameDTO.toGame())
                }
            }
    }
}