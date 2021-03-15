package com.g13.DRAG.local

fun getWord(cache: MutableList<String>) : String{

    val rnds = (0 until cache.size).random()

    return cache[rnds]
}