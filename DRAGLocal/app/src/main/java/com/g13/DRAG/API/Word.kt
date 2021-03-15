package com.g13.DRAG.API

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents the random word. It is the application's model.
 *
 * @property id           id of the word
 * @property word        the word
 */
@Parcelize
data class WordInfo(
    val word: String
): Parcelable

/**
 * Maps an array of words into a list of WordInfos
 */
fun modelFromDTO(dto: Array<Word>): MutableList<String> {
    return dto.map {
        it.word
    } as MutableList<String>
}