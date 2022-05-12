package com.example.ui.views.toolbar.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar

class TransparentToolBar : Toolbar, ITransparentDelegate {

    private var mTransparentDelegate: TransparentDelegate? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mTransparentDelegate = TransparentDelegate(this)
    }

    override fun updateTop(top: Float) {
        mTransparentDelegate!!.updateTop(top)
    }

    override fun setMaxOffset(offset: Float) {
        mTransparentDelegate!!.setMaxOffset(offset)
    }

    override fun setMidOffset(offset: Float) {
        TODO("Not yet implemented")
    }

    override fun setColorToBackGround(backGroundColor: Int) {
        mTransparentDelegate!!.setColorToBackGround(backGroundColor)
    }

    fun addOnScrollStateListener(listener: OnTransparentListener) {
        mTransparentDelegate!!.addOnScrollStateListener(listener)
    }

    fun removeOnScrollStateListener(listener: OnTransparentListener) {
        mTransparentDelegate!!.removeOnScrollStateListener(listener)
    }

    fun removeOnScrollStateListenerAll() {
        mTransparentDelegate!!.removeOnScrollStateListenerAll()
    }
}