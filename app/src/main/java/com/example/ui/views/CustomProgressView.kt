package com.example.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Keep

class CustomProgressView(context: Context?) : View(context) {
    private var lastUpdateTime: Long = 0
    private var radOffset = 0f
    private var currentCircleLength = 0f
    private var risingCircleLength = false
    private var currentProgressTime = 0f
    private val circleRect = RectF()
    private var useSelfAlpha = false
    private var drawingCircleLenght = 0f
    private var progressColor: Int
    private val decelerateInterpolator: DecelerateInterpolator
    private val accelerateInterpolator: AccelerateInterpolator
    private val progressPaint: Paint
    private var size: Int
    private var currentProgress = 0f
    private var progressAnimationStart = 0f
    private var progressTime = 0
    private var animatedProgress = 0f
    private var toCircle = false
    private var toCircleProgress = 0f
    private var noProgress = true
    fun setUseSelfAlpha(value: Boolean) {
        useSelfAlpha = value
    }

    @Keep
    override fun setAlpha(alpha: Float) {
        super.setAlpha(alpha)
        if (useSelfAlpha) {
            val background = background
            val a = (alpha * 255).toInt()
            if (background != null) {
                background.alpha = a
            }
            progressPaint.alpha = a
        }
    }

    fun setNoProgress(value: Boolean) {
        noProgress = value
    }

    fun setProgress(value: Float) {
        currentProgress = value
        if (animatedProgress > value) {
            animatedProgress = value
        }
        progressAnimationStart = animatedProgress
        progressTime = 0
    }

    fun sync(from: CustomProgressView) {
        lastUpdateTime = from.lastUpdateTime
        radOffset = from.radOffset
        toCircle = from.toCircle
        toCircleProgress = from.toCircleProgress
        noProgress = from.noProgress
        currentCircleLength = from.currentCircleLength
        drawingCircleLenght = from.drawingCircleLenght
        currentProgressTime = from.currentProgressTime
        currentProgress = from.currentProgress
        progressTime = from.progressTime
        animatedProgress = from.animatedProgress
        risingCircleLength = from.risingCircleLength
        progressAnimationStart = from.progressAnimationStart
        updateAnimation((17 * 5).toLong())
    }

    private fun updateAnimation() {
        val newTime = System.currentTimeMillis()
        var dt = newTime - lastUpdateTime
        if (dt > 17) {
            dt = 17
        }
        lastUpdateTime = newTime
        updateAnimation(dt)
    }

    private fun updateAnimation(dt: Long) {
        radOffset += 360 * dt / rotationTime
        val count = (radOffset / 360).toInt()
        radOffset -= (count * 360).toFloat()
        if (toCircle && toCircleProgress != 1f) {
            toCircleProgress += 16 / 220f
            if (toCircleProgress > 1f) {
                toCircleProgress = 1f
            }
        } else if (!toCircle && toCircleProgress != 0f) {
            toCircleProgress -= 16 / 400f
            if (toCircleProgress < 0) {
                toCircleProgress = 0f
            }
        }
        if (noProgress) {
            if (toCircleProgress == 0f) {
                currentProgressTime += dt.toFloat()
                if (currentProgressTime >= risingTime) {
                    currentProgressTime = risingTime
                }
                currentCircleLength = if (risingCircleLength) {
                    4 + 266 * accelerateInterpolator.getInterpolation(currentProgressTime / risingTime)
                } else {
                    4 - 270 * (1.0f - decelerateInterpolator.getInterpolation(currentProgressTime / risingTime))
                }
                if (currentProgressTime == risingTime) {
                    if (risingCircleLength) {
                        radOffset += 270f
                        currentCircleLength = -266f
                    }
                    risingCircleLength = !risingCircleLength
                    currentProgressTime = 0f
                }
            } else {
                if (risingCircleLength) {
                    val old = currentCircleLength
                    currentCircleLength =
                        4 + 266 * accelerateInterpolator.getInterpolation(currentProgressTime / risingTime)
                    currentCircleLength += 360 * toCircleProgress
                    val dx = old - currentCircleLength
                    if (dx > 0) {
                        radOffset += old - currentCircleLength
                    }
                } else {
                    val old = currentCircleLength
                    currentCircleLength = 4 - 270 * (1.0f - decelerateInterpolator.getInterpolation(
                        currentProgressTime / risingTime
                    ))
                    currentCircleLength -= 364 * toCircleProgress
                    val dx = old - currentCircleLength
                    if (dx > 0) {
                        radOffset += old - currentCircleLength
                    }
                }
            }
        } else {
            val progressDiff = currentProgress - progressAnimationStart
            if (progressDiff > 0) {
                progressTime += dt.toInt()
                if (progressTime >= 200.0f) {
                    progressAnimationStart = currentProgress
                    animatedProgress = progressAnimationStart
                    progressTime = 0
                } else {
                    animatedProgress =
                        progressAnimationStart + progressDiff * getInterpolation(progressTime / 200.0f)
                }
            }
            currentCircleLength = Math.max(4f, 360 * animatedProgress)
        }
        invalidate()
    }

    fun setSize(value: Int) {
        size = value
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        progressPaint.color = progressColor
    }

    override fun onDraw(canvas: Canvas) {
        val x = (measuredWidth - size) / 2
        val y = (measuredHeight - size) / 2
        circleRect[x.toFloat(), y.toFloat(), (x + size).toFloat()] = (y + size).toFloat()
        canvas.drawArc(
            circleRect,
            radOffset,
            currentCircleLength.also { drawingCircleLenght = it },
            false,
            progressPaint
        )
        updateAnimation()
    }

    fun draw(canvas: Canvas, cx: Float, cy: Float) {
        circleRect[cx - size / 2f, cy - size / 2f, cx + size / 2f] = cy + size / 2f
        canvas.drawArc(
            circleRect,
            radOffset,
            currentCircleLength.also { drawingCircleLenght = it },
            false,
            progressPaint
        )
        updateAnimation()
    }

    private fun getInterpolation(input: Float): Float {
        val result: Float = if (currentProgress == 1.0f) {
            (1.0f - (1.0f - input) * (1.0f - input))
        } else {
            (1.0f - Math.pow((1.0f - input).toDouble(), (2 * currentProgress).toDouble())).toFloat()
        }
        return result
    }

    companion object {
        private const val rotationTime = 2000f
        private const val risingTime = 500f
        fun dp(value: Float): Int {
            val density = 1f
            return if (value == 0f) {
                0
            } else Math.ceil((density * value).toDouble()).toInt()
        }
    }

    init {
        size = dp(50f)
        progressColor = Color.RED
        decelerateInterpolator = DecelerateInterpolator()
        accelerateInterpolator = AccelerateInterpolator()
        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.strokeWidth = dp(12f).toFloat()
        progressPaint.color = progressColor
    }
}