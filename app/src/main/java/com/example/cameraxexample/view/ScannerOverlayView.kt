package com.example.cameraxexample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ScannerOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val rectPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = canvas.width / 2
        val centerY = canvas.height / 2
        val boxWidth = canvas.width * 0.8f
        val boxHeight = boxWidth * 0.7f

        val left = centerX - boxWidth / 2
        val top = centerY - boxHeight / 2
        val right = centerX + boxWidth / 2
        val bottom = centerY + boxHeight / 2

        canvas.drawRect(left, top, right, bottom, rectPaint)
    }


}