package com.g13.DRAG.HighScores

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.g13.DRAG.local.TAG
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * The path of the Firestore collection that contains all the challenges
 */
private const val HIGHSCORES_COLLECTION = "High Scores"

private const val PLAYER_NAME = "Player"
private const val HIGH_SCORE = "High Score"
private const val FIRST_WORD = "First Word"

/**
 * Extension function used to convert createdChallenge documents stored in the Firestore DB into
 * [HighScoreInfo] instances
 */


private fun QueryDocumentSnapshot.toChallengeInfo() =
   HighScoreInfo(
       id,
       data[PLAYER_NAME] as String,
       data[HIGH_SCORE] as String,
       data[FIRST_WORD] as String
   )

class HighScoresRepository(private val mapper: ObjectMapper){

   /**
    * Fetches the list of open challenges from the backend
    *
    * Implementation note: This fetches ALL open challenges. In realistic scenarios this is
    * a poor design decision because the resulting data set size is unbounded!
    */
  fun fetchScores(onSuccess: (List<HighScoreInfo>) -> Unit, onError: (Exception) -> Unit) {
       Firebase.firestore.collection(HIGHSCORES_COLLECTION)
           .get()
           .addOnSuccessListener { result ->
               Log.v(TAG, "Repo got list from Firestore: $result")
               onSuccess(result.map { it.toChallengeInfo() }.toList())
           }
           .addOnFailureListener {
               Log.e(TAG, "Repo: An error occurred while fetching list from Firestore")
               Log.e(TAG, "Error was $it")
               onError(it)
           }
   }

   /**
    * Publishes a high score with the given [name], [score] and [firstWord]
    */
   fun publishHighScore(
       name: String,
       message: String,
       firstWord:String,
       onSuccess: (HighScoreInfo) -> Unit,
       onError: (Exception) -> Unit) {

       Firebase.firestore.collection(HIGHSCORES_COLLECTION)
           .add(hashMapOf(PLAYER_NAME to name, HIGH_SCORE to message, FIRST_WORD to firstWord))
           .addOnSuccessListener { onSuccess(HighScoreInfo(it.id, name, message, firstWord)) }
           .addOnFailureListener { onError(it) }
   }
}