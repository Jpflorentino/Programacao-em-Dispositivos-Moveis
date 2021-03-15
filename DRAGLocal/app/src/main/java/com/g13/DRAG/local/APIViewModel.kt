package com.g13.DRAG.local

import android.app.Application
import android.util.Log
import androidx.lifecycle.*

class APIViewModel(application: Application) : AndroidViewModel(application) {

    private val app by lazy { application as GameApplication }

    private val wordRepository by lazy{app.wordRepository}

    private val gameRepository by lazy{app.gameRepository}

    enum class State {
        IDLE,
        IN_PROGRESS,
        COMPLETE
    }

    val state: LiveData<State> = MutableLiveData(State.IDLE)

    val cache : MutableLiveData<MutableList<String>> = MediatorLiveData()

    var firstWord : String = ""

    /**
     * Asynchronously fetches 30 words
     */
    private fun fetchWords(): LiveData<MutableList<String>>{

        val app = getApplication<GameApplication>()

        if (state.value == State.IN_PROGRESS)
            throw IllegalStateException()

        (state as MutableLiveData<State>).value = State.IN_PROGRESS

        val mediator = cache as MediatorLiveData<MutableList<String>>

        val source = app.wordRepository.fetchWordsInfo()

        mediator.addSource(source){ wordList ->

            state.value = State.COMPLETE

            cache.value = wordList.getOrNull()

            mediator.removeSource(source)

        }

        return cache
    }

    fun addWordsToRepoCache(list: MutableList<String>){
        wordRepository.cache = list.toMutableSet() //save to cache in shared
    }

    fun setToIdle() {
        (state as MutableLiveData<State>).value = State.IDLE
    }

    fun setToComplete() {
        (state as MutableLiveData<State>).value = State.COMPLETE
    }

    fun getFirstWord(){

        if(wordRepository.cache.isEmpty()){

            //set in motion the cache request to the API
                if (state.value != State.IN_PROGRESS && state.value != State.COMPLETE)
                    fetchWords()

        }else{

            gameRepository.firstWord = getWord(wordRepository.cache.toMutableList())

            wordRepository.cache.remove(gameRepository.firstWord)

            firstWord = gameRepository.firstWord

            setToComplete()
        }
    }
}