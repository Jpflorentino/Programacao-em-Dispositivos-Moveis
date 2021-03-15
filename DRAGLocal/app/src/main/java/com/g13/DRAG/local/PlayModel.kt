package com.g13.DRAG.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Point(val x: Float, val y: Float) : Parcelable

@Parcelize
data class Line(var points : List<Point>) : Parcelable

data class PlayModel(var lines: List<Line>)