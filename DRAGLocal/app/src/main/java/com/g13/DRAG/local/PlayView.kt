package com.g13.DRAG.local

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PlayView(ctx: Context, attrs: AttributeSet?): View(ctx,attrs){

    var model: PlayModel? = null
        set(value) {
            field = value
            invalidate() //invalidate() "refreshes" the view; it tells the app it has to be "redrawn"; without invalidate the drawing doesn't show on the view
        }

    private val brush: Paint = Paint().apply {
        color = Color.parseColor("#FF0000")
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {

        val localModel = model
        if (localModel != null) {

            for(line in localModel.lines){
                for(point in line.points){

                    lateinit var secondPoint : Point

                    if(line.points.indexOf(point) + 1 < line.points.size)
                        secondPoint = line.points[(line.points.indexOf(point) + 1)]
                    else
                        secondPoint = point

                    canvas?.drawLine(point.x, point.y, secondPoint.x, secondPoint.y, brush)
                    //canvas?.drawPoint(point.x, point.y, brush)
                }
            }

        }
    }

}


//model?.path?.let { canvas?.drawPath(it, brush) }
//model?.points?.let { canvas?.drawLine(it, brush) }