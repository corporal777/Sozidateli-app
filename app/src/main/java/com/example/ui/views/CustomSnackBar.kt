package com.example.ui.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.example.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.api.Context

class CustomSnackBar(
    parent: ViewGroup,
    content: View,
    contentViewCallback: ContentViewCallback
) : BaseTransientBottomBar<CustomSnackBar>(parent, content, contentViewCallback) {

    companion object{
        @SuppressLint("ResourceAsColor")
        fun make(parent: ViewGroup, duration: Int): CustomSnackBar {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val content: View = inflater.inflate(R.layout.layout_custom_snack_bar, parent, false)
            val viewCallback = ContentViewCallback(content)
            val customSnackbar = CustomSnackBar(parent, content, viewCallback)
            customSnackbar.view.setBackgroundColor(android.R.color.transparent)
            customSnackbar.getView().setPadding(0, 0, 0, 0)
            customSnackbar.setDuration(duration)
            return customSnackbar
        }
    }

    fun setText(text: CharSequence?): CustomSnackBar {
        val textView = getView().findViewById<View>(R.id.tv_message) as TextView
        textView.setText(text)
        return this
    }

    fun setIcon(icon: Int): CustomSnackBar? {
        val imageView = getView().findViewById<View>(R.id.iv_icon) as ImageView
        imageView.isVisible = true
        imageView.setImageResource(icon)
        return this
    }

    class ContentViewCallback(content: View) : BaseTransientBottomBar.ContentViewCallback {
        private val content: View
        override fun animateContentIn(delay: Int, duration: Int) {
            ViewCompat.setScaleY(content, 0f)
            ViewCompat.animate(content).scaleY(1f).setDuration(duration.toLong())
                .setStartDelay(delay.toLong())
        }

        override fun animateContentOut(delay: Int, duration: Int) {
            ViewCompat.setScaleY(content, 1f)
            ViewCompat.animate(content).scaleY(0f).setDuration(duration.toLong())
                .setStartDelay(delay.toLong())
        }

        init {
            this.content = content
        }
    }

}