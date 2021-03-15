package com.g13.DRAG.HighScores

enum class State { IDLE, ONGOING, COMPLETE }

data class Result<R, E>(
    val state: State = State.IDLE,
    val result: R? = null,
    val error: E? = null
)