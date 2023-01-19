package com.generator.qrcodegenerator.style

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun interface QrCanvasShape {


    fun draw(canvas: Canvas, drawPaint : Paint, erasePaint : Paint)
}


fun QrCanvasShape.toShapeModifier(elementSize : Int) : QrShapeModifier =
    QrCanvasToShapeModifier(elementSize, this)



private class QrCanvasToShapeModifier(
    private val size: Int,
    private val canvasShapeModifier : QrCanvasShape
) : QrShapeModifier {
    private val drawPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }
    private val erasePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private val pixels by lazy {
        val pixels = IntArray(size * size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            eraseColor(erasePaint.color)
        }.applyCanvas { canvasShapeModifier.draw(this, drawPaint, erasePaint) }

        bitmap.getPixels(pixels, 0, size, 0,0, size,size)
        bitmap.recycle()
        pixels
    }

    override fun invoke(
        i: Int, j: Int, elementSize: Int, neighbors: Neighbors
    ): Boolean {

        val scale = size / elementSize.toFloat()
        val sI = (i * scale).toInt()
        val sJ = (j * scale).toInt()

        return pixels[sI + size * sJ] == drawPaint.color
    }
}