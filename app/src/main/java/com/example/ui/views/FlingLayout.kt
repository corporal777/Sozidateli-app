package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

typealias PositionChangeListener = (top: Int, left: Int, positionRangeRate: Float) -> Unit
typealias DismissListener = () -> Unit

class FlingLayout : FrameLayout {

    var isDragEnabled: Boolean = true
    var isDismissEnabled: Boolean = true
    var positionChangeListener: PositionChangeListener? = null
    var dismissListener: DismissListener? = null
    private val threshold: Int = 1500
    private var dragHelper: ViewDragHelper? = null
    private var defaultChildX: Int? = null
    private var defaultChildY: Int? = null
    private var positionRangeRate: Float? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        dragHelper = ViewDragHelper.create(this, 1F, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return isDragEnabled && child.visibility == View.VISIBLE
            }


            override fun getViewVerticalDragRange(child: View): Int {
                return 1
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top
            }

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)

                val defaultChildY = defaultChildY ?: return
                val rangeY = (measuredHeight / 2)
                val distance = abs(top - defaultChildY)

                positionRangeRate = 1F.coerceAtMost(distance.toFloat() / rangeY)
                positionChangeListener?.invoke(top, left, positionRangeRate ?: 0F)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                releasedChild.let { this@FlingLayout.onViewReleased(it, yvel) }
            }
        })
    }

    private fun onViewReleased(target: View, yvel: Float) {
        positionRangeRate = null

        val x = defaultChildX ?: return
        val y = defaultChildY ?: return

        if (abs(yvel) < threshold) {
            dragHelper?.settleCapturedViewAt(x, y)
            invalidate()
            return
        }

        if (!isDismissEnabled) return

        val targetY = if (yvel > 0) measuredHeight else -target.measuredHeight

        dragHelper?.smoothSlideViewTo(target, x, targetY)
        invalidate()

        dismissListener?.invoke()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return event.pointerCount <= 1 && dragHelper?.shouldInterceptTouchEvent(event) ?: false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper?.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (childCount == 0) return
        check(childCount == 1) { "child must be single: current child count=$childCount" }

        defaultChildX = getChildAt(0)?.left
        defaultChildY = getChildAt(0)?.top
    }

    override fun computeScroll() {
        super.computeScroll()
        if (dragHelper?.continueSettling(true) == true) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }
}