package com.example.ui.views.loading

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout


class LoadingIndicatorView : RelativeLayout {

    private var numberOfBars: Int = 0
    private var arrBars = arrayListOf<LoadingIndicatorBarView>()

    var radius: Float = 0f
    private var isAnimating = false
    private var currentFrame = 0
    private val mHandler = Handler()
    private var playFrameRunnable: Runnable? = Runnable {  }

    constructor(context: Context, cornerRadius: Float) : super(context) {
        this.radius = cornerRadius
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {

        numberOfBars = 12
        initViews()
        initLayouts()
        addViews()
        spreadBars()
    }

    fun initViews() {
        arrBars = ArrayList<LoadingIndicatorBarView>()
        for (i in 0 until numberOfBars) {
            val bar = LoadingIndicatorBarView(context, radius / 10.0f)
            arrBars!!.add(bar)
        }
    }

    fun initLayouts() {
        for (i in 0 until numberOfBars) {
            val bar: LoadingIndicatorBarView = arrBars[i]
            bar.setId(View.generateViewId())
            val barLayoutParams: LayoutParams =
                LayoutParams((radius / 5.0f).toInt(), (radius / 2.0f).toInt())
            barLayoutParams.addRule(ALIGN_PARENT_TOP)
            barLayoutParams.addRule(CENTER_HORIZONTAL)
            bar.setLayoutParams(barLayoutParams)
        }
    }

    fun addViews() {
        for (i in 0 until numberOfBars) {
            val bar: LoadingIndicatorBarView = arrBars[i]
            addView(bar)
        }
    }

    fun spreadBars() {
        var degrees = 0
        for (i in 0 until arrBars.size) {
            val bar: LoadingIndicatorBarView = arrBars[i]
            rotateBar(bar, degrees.toFloat())
            degrees += 30
        }
    }

    private fun rotateBar(bar: LoadingIndicatorBarView, degrees: Float) {
        val animation = RotateAnimation(0F, degrees, radius / 10.0f, radius)
        animation.setDuration(0)
        animation.setFillAfter(true)
        bar.setAnimation(animation)
        animation.start()
    }

    fun startAnimating() {
        setAlpha(1.0f)
        isAnimating = true
        playFrameRunnable = Runnable { playFrame() }

        // recursive function until isAnimating is false
        playFrame()
    }

    fun stopAnimating() {
        isAnimating = false
        setAlpha(0.0f)
        invalidate()
        playFrameRunnable = null
    }

    private fun playFrame() {
        if (isAnimating) {
            resetAllBarAlpha()
            updateFrame()
            mHandler.postDelayed(playFrameRunnable!!, 0)
        }
    }

    private fun updateFrame() {
        if (isAnimating) {
            showFrame(currentFrame)
            currentFrame += 1
            if (currentFrame > 11) {
                currentFrame = 0
            }
        }
    }

    private fun resetAllBarAlpha() {
        var bar: LoadingIndicatorBarView? = null
        for (i in 0 until arrBars.size) {
            bar = arrBars[i]
            bar.setAlpha(0.5f)
        }
    }

    private fun showFrame(frameNumber: Int) {
        val indexes = getFrameIndexesForFrameNumber(frameNumber)
        gradientColorBarSets(indexes)
    }

    private fun getFrameIndexesForFrameNumber(frameNumber: Int): IntArray {
        return if (frameNumber == 0) {
            indexesFromNumbers(0, 11, 10, 9)
        } else if (frameNumber == 1) {
            indexesFromNumbers(1, 0, 11, 10)
        } else if (frameNumber == 2) {
            indexesFromNumbers(2, 1, 0, 11)
        } else if (frameNumber == 3) {
            indexesFromNumbers(3, 2, 1, 0)
        } else if (frameNumber == 4) {
            indexesFromNumbers(4, 3, 2, 1)
        } else if (frameNumber == 5) {
            indexesFromNumbers(5, 4, 3, 2)
        } else if (frameNumber == 6) {
            indexesFromNumbers(6, 5, 4, 3)
        } else if (frameNumber == 7) {
            indexesFromNumbers(7, 6, 5, 4)
        } else if (frameNumber == 8) {
            indexesFromNumbers(8, 7, 6, 5)
        } else if (frameNumber == 9) {
            indexesFromNumbers(9, 8, 7, 6)
        } else if (frameNumber == 10) {
            indexesFromNumbers(10, 9, 8, 7)
        } else {
            indexesFromNumbers(11, 10, 9, 8)
        }
    }

    private fun indexesFromNumbers(i1: Int, i2: Int, i3: Int, i4: Int): IntArray {
        return intArrayOf(i1, i2, i3, i4)
    }

    private fun gradientColorBarSets(indexes: IntArray) {
        var alpha = 1.0f
        var barView: LoadingIndicatorBarView? = null
        for (i in indexes.indices) {
            val barIndex = indexes[i]
            barView = arrBars[barIndex]
            barView.setAlpha(alpha)
            alpha -= 0.125f
        }
        invalidate()
    }
}