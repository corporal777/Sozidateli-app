package com.example.ui.views.toolbar.widget

import android.graphics.Color
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat

class TransparentDelegate(private val mRootView: View) : ViewTreeObserver.OnGlobalLayoutListener,
    ITransparentDelegate {
    private val mOnTransparentListeners = ArrayList<OnTransparentListener>()

    private var maxOffset: Float = 0.toFloat()
    private var midOffset: Float = 0.toFloat()

    private val mNormalGroundColor: Int = ContextCompat.getColor(mRootView.context,
        android.R.color.transparent)
    private var mBackGroundColor: Int = 0

    init {
        mBackGroundColor = mNormalGroundColor
        mRootView.viewTreeObserver.removeGlobalOnLayoutListener(this)
    }

    override fun onGlobalLayout() {
        mRootView.setBackgroundColor(mBackGroundColor)
        mRootView.viewTreeObserver.removeGlobalOnLayoutListener(this)
    }

    fun addOnScrollStateListener(listener: OnTransparentListener?) {
        if (listener != null) {
            if (!mOnTransparentListeners.contains(listener)) {
                mOnTransparentListeners.add(listener)
            }
        }
    }

    fun removeOnScrollStateListener(listener: OnTransparentListener?) {
        if (listener != null) {
            if (mOnTransparentListeners.contains(listener)) {
                mOnTransparentListeners.remove(listener)
            }
        }
    }

    fun removeOnScrollStateListenerAll() {
        mOnTransparentListeners.clear()
    }

    private fun changeAlpha(color: Int, fraction: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = (Color.alpha(color) * fraction).toInt()
        return Color.argb(alpha, red, green, blue)
    }

    override fun updateTop(top: Float) {
        if (maxOffset <= 0 && mBackGroundColor == mNormalGroundColor) {
            return
        }
        var fraction = top / maxOffset

        if (fraction <= MIN_FRACTION || fraction < MID_FRACTION) {
            fraction = MIN_FRACTION.toFloat()
            for (listener in mOnTransparentListeners) {
                listener.onTransparentStart(top)
            }
        }

        //if (fraction >= MID_FRACTION && fraction < MAX_FRACTION){
        if (fraction >= MID_FRACTION && fraction < 4){
            fraction = MID_FRACTION.toFloat()
            for (listener in mOnTransparentListeners) {
                //listener.onTransparentMiddle(fraction)
                listener.onTransparentMiddle(top)
            }
        }
        //if (fraction >= MORE_MID_FRACTION && fraction < MAX_FRACTION){
        if (fraction >= MORE_MID_FRACTION && fraction < 5.8){
            fraction = MORE_MID_FRACTION.toFloat()
            for (listener in mOnTransparentListeners) {
                //listener.onTransparentMiddle(fraction)
                listener.onTransparentMoreMiddle(top)
            }
        }


        if (fraction >= 5.8 && fraction < MORE_MAX_FRACTION) {
            fraction = MAX_FRACTION.toFloat()
            for (listener in mOnTransparentListeners) {
                //listener.onTransparentEnd(fraction)
                listener.onTransparentEnd(top)
            }
        }
        if (fraction >= MORE_MAX_FRACTION){
            fraction = MORE_MAX_FRACTION.toFloat()
            for (listener in mOnTransparentListeners) {
                //listener.onTransparentEnd(fraction)
                listener.onTransparentMoreEnd(top)
            }
        }
        val newColor = changeAlpha(mBackGroundColor, fraction)
        mRootView.setBackgroundColor(newColor)

        for (listener in mOnTransparentListeners) {
            listener.onTransparentUpdateFraction(fraction)
        }
    }

    override fun setMaxOffset(offset: Float) {
        this.maxOffset = offset
    }

    override fun setMidOffset(offset: Float) {
        this.midOffset = offset
    }

    override fun setColorToBackGround(backGroundColor: Int) {
        this.mBackGroundColor = backGroundColor
    }

    fun getColorToBackGround(): Int {
        return mBackGroundColor
    }

    companion object {

        private const val MIN_FRACTION = 0
        private const val MID_FRACTION = 1.2
        private const val MORE_MID_FRACTION = 5
        private const val MAX_FRACTION = 6
        private const val MORE_MAX_FRACTION = 8.0
    }
}