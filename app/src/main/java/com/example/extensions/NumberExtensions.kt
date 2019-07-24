package com.example.extensions

import android.content.res.Resources

val Float.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Float.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
val Float.px: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Int.dp: Int
    get() = this.toFloat().dp.toInt()
val Int.sp: Int
    get() = this.toFloat().sp.toInt()
val Int.px: Int
    get() = this.toFloat().px.toInt()