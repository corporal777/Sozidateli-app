package com.example.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.app.R
import com.example.app.databinding.LayoutCustomTextInputViewBinding
import com.example.app.databinding.LayoutMessageSnackBarBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback

class MessageSnackBar : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = LayoutMessageSnackBarBinding.inflate(LayoutInflater.from(context), this)

    fun setText(text: CharSequence?) = binding.tvMessage.setText(text)

    fun setIcon(icon: Int) = binding.ivIcon.setImageResource(icon)

    fun show(root : ViewGroup){
        TransitionManager.beginDelayedTransition(root, Slide(Gravity.BOTTOM))
        isVisible = true
    }

    fun hide(root : ViewGroup){
        TransitionManager.beginDelayedTransition(root, Slide(Gravity.BOTTOM))
        isVisible = false
    }
}