package com.example.ui.views.dialogs_new.blur

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import com.example.extensions.dp


class AppUtils {


    companion object {


        private fun takeScreenShot(activity: Activity, callback: (Bitmap) -> Unit) {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()


            val b1 = view.drawingCache
            val frame = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top

            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val width = 250.dp
            val height = 250.dp


            val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
            view.destroyDrawingCache()

            callback(b)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun takeScreenShotOreo(
            view: View,
            window: Window,
            bitmapCallback: (Bitmap) -> Unit
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Above Android O, use PixelCopy
                val bitmap = Bitmap.createBitmap(250.dp, 250.dp, Bitmap.Config.ARGB_8888)
                val location = IntArray(2)
                view.getLocationInWindow(location)
                val rect = Rect(
                    location[0] + 100.dp,
                    location[1] + 250.dp,
                    location[0] + 450.dp, location[1] + 450.dp)

                Log.e("TOP", rect.top.toString())
                Log.e("LEFT", rect.left.toString())
                Log.e("BOTTOM", rect.bottom.toString())
                Log.e("RIGHT", rect.right.toString())
                PixelCopy.request(
                    window,
                    rect,
                    bitmap,
                    {
                        if (it == PixelCopy.SUCCESS) {
                            bitmapCallback.invoke(bitmap)
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } else {
                val tBitmap = Bitmap.createBitmap(
                    view.width, view.height, Bitmap.Config.RGB_565
                )
                val canvas = Canvas(tBitmap)
                view.draw(canvas)
                canvas.setBitmap(null)
                bitmapCallback.invoke(tBitmap)
            }
        }


        fun screenShot(view: View, activity: Activity, callback: (Bitmap) -> Unit) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) takeScreenShotOreo(
                view,

                activity.window,
                callback
            )
            else takeScreenShot(activity, callback)

    }
}