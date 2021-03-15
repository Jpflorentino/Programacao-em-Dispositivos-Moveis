package com.g13.DRAG.API

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.fasterxml.jackson.databind.ObjectMapper

private const val CACHE_KEY = "CacheKey"

/**
 * The application's repository holding globally relevant information.
 *
 * Implementation note: Currently the information is always fetched from the remote Web API, but it
 * could be cached (with an expiration date) for future use.
 */
class WordRepository(
    private val sharedPreferences: SharedPreferences,
    private val queue: RequestQueue,
    private val mapper: ObjectMapper
) {

    //Cache with the words fetched from the API
    var cache : MutableSet<String>
        get() = sharedPreferences.getStringSet(CACHE_KEY, mutableSetOf()) as MutableSet<String>
        set(value){
            sharedPreferences
                .edit()
                .putStringSet(CACHE_KEY, value)
                .apply()
        }

    /**
     * Asynchronous operation for fetching the list of 30 new words
     *
     *
     * @return the result promise
     */
     fun fetchWordsInfo(): LiveData<Result<MutableList<String>?>> {
        val result = MutableLiveData<Result<MutableList<String>?>>()
        val request = GetWordInfoRequest(
            URL_30_WORDS,
            mapper,
            {
                result.value = Result.success(modelFromDTO(it))
            },
            {
                result.value = Result.failure(it)
            }
        )
        queue.add(request)
        return result
    }
}