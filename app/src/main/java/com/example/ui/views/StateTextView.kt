package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

open class StateTextView : AppCompatTextView {

    private var stateArray: Array<IntArray>? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setState(stateArray: Array<IntArray>) {
        this.stateArray = stateArray
        refreshDrawableState()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = stateArray ?: emptyArray()
        val drawableState = super.onCreateDrawableState(extraSpace + states.size)
        states.forEach { View.mergeDrawableStates(drawableState, it) }
        return drawableState
    }
}