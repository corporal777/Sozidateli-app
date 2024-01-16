package com.example.ui.stories

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.example.extensions.dp
import kotlin.math.abs

class StoriesTouchListener(val targetView: View, val topView: View) : View.OnTouchListener {


    private val initialHeight = targetView.height
    private val initialWidth = targetView.width
    private val initialX = 0
    private val initialY = targetView.translationY
    private var oldY = targetView.y
    private val list = arrayListOf<Int>().apply {
        for (i in 0 until 2000) {
            this.add(i)
        }
    }
    private var counter = 0

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val X = event!!.rawX.toInt()
        val y = abs(event.rawY)



        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_UP -> {
                targetView.y = initialY
                oldY = initialY
                counter = 0
            }
            MotionEvent.ACTION_POINTER_DOWN -> {}
            MotionEvent.ACTION_POINTER_UP -> {}
            MotionEvent.ACTION_MOVE -> {

//                if (oldY < y) targetView.y = targetView.y + 5
//                else{
//                    if (targetView.y >= 0) targetView.y = targetView.y - 5
//                }
//                oldY = y

                if (oldY < y) counter += 1
                else{
                    if (counter > 0) counter -= 1
                }
                oldY = y
//
                targetView.y = list.get(counter).toFloat() * 10
                targetView.updateLayoutParams<FrameLayout.LayoutParams> {
                    width = initialWidth - counter
                }
            }
        }

        return true
    }

}