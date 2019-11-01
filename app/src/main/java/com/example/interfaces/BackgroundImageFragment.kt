package com.example.interfaces

import android.graphics.drawable.Drawable

interface BackgroundImageFragment {
    val isLightStatus: Boolean
    fun getFragmentBackgroundDrawable(): Drawable?
}