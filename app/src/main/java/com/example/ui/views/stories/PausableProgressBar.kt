package com.example.ui.views.stories

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import com.example.app.R

internal class PausableProgressBar : FrameLayout {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val frontProgressView: View
    private val maxProgressView: View
    private var animation: PausableScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION.toLong()
    private var callback: Callback? = null

    internal interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }


    init {
        LayoutInflater.from(context).inflate(R.layout.layout_stories_progress_view, this)
        frontProgressView = findViewById(R.id.front_progress)
        maxProgressView = findViewById(R.id.max_progress)
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        maxProgressView.setBackgroundResource(R.drawable.background_stories_progress_max)
        maxProgressView.setVisibility(VISIBLE)
        if (animation != null) {
            animation?.setAnimationListener(null)
            animation?.cancel()
        }
    }

    fun setMaxWithoutCallback() {
        maxProgressView.setBackgroundResource(R.drawable.background_stories_progress_max)
        maxProgressView.setVisibility(VISIBLE)
        if (animation != null) {
            animation?.setAnimationListener(null)
            animation?.cancel()
        }
    }

    private fun finishProgress(isMax: Boolean) {
        if (isMax) maxProgressView.setBackgroundResource(R.drawable.background_stories_progress_primary)
        maxProgressView.visibility = if (isMax) VISIBLE else GONE
        if (animation != null) {
            animation?.setAnimationListener(null)
            animation?.cancel()
            if (callback != null) {
                callback!!.onFinishProgress()
            }
        }
    }

    fun startProgress() {
        maxProgressView.setVisibility(GONE)
        animation = PausableScaleAnimation(
            0f,
            1f,
            1f,
            1f,
            Animation.ABSOLUTE,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        animation?.duration = duration
        animation?.interpolator = LinearInterpolator()
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                frontProgressView.setVisibility(View.VISIBLE)
                if (callback != null) callback!!.onStartProgress()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (callback != null) callback!!.onFinishProgress()
            }
        })
        animation?.fillAfter = true
        frontProgressView.startAnimation(animation)
    }

    fun pauseProgress() {
        if (animation != null) animation?.pause()
    }

    fun resumeProgress() {
        if (animation != null) animation?.resume()
    }

    fun clear() {
        if (animation != null) {
            animation!!.setAnimationListener(null)
            animation!!.cancel()
            animation = null
        }
    }

    private inner class PausableScaleAnimation internal constructor(
        fromX: Float, toX: Float, fromY: Float,
        toY: Float, pivotXType: Int, pivotXValue: Float, pivotYType: Int,
        pivotYValue: Float
    ) :
        ScaleAnimation(
            fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
            pivotYValue
        ) {
        private var mElapsedAtPause: Long = 0
        private var mPaused = false
        override fun getTransformation(
            currentTime: Long,
            outTransformation: Transformation?,
            scale: Float
        ): Boolean {
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - getStartTime()
            }
            if (mPaused) {
                setStartTime(currentTime - mElapsedAtPause)
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }


        fun pause() {
            if (mPaused) return
            mElapsedAtPause = 0
            mPaused = true
        }


        fun resume() {
            mPaused = false
        }
    }

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 2000
    }
}