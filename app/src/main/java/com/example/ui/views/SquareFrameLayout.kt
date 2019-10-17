package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(width: Int, height: Int) {
        val widthSize = MeasureSpec.getSize(width)
        val heightSize = MeasureSpec.getSize(height)

        if (widthSize == 0 && heightSize == 0) {
            // If there are no constraints on size, let FrameLayout measure
            super.onMeasure(width, height)

            // Now use the smallest of the measured dimensions for both dimensions
            val minSize = measuredWidth.coerceAtMost(measuredHeight)
            setMeasuredDimension(minSize, minSize)
            return
        }

        val size = if (widthSize == 0 || heightSize == 0) {
            // If one of the dimensions has no restriction on size, set both dimensions to be the
            // on that does
            widthSize.coerceAtLeast(heightSize)
        } else {
            // Both dimensions have restrictions on size, set both dimensions to be the
            // smallest of the two
            widthSize.coerceAtMost(heightSize)
        }

        val newMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(newMeasureSpec, newMeasureSpec)
    }
}