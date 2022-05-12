package com.example.ui.views.toolbar.widget

interface OnTransparentListener {
    fun onTransparentStart(fraction: Float)

    fun onTransparentMiddle(fraction: Float)

    fun onTransparentMoreMiddle(fraction: Float)

    fun onTransparentEnd(fraction: Float)
    fun onTransparentMoreEnd(fraction: Float)

    fun onTransparentUpdateFraction(fraction: Float)
}