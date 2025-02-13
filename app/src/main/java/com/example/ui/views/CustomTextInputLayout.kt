package com.example.ui.views

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDrawableOrThrow
import com.example.app.R
import com.example.extensions.dp
import com.example.util.getColorStateList
import com.example.util.getDrawable
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout : TextInputLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var oldIconDrawable: Drawable? = null
    private var oldIconMode: Int? = null
    private var oldIconTint: Drawable? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        findViewById<View>(R.id.text_input_end_icon)?.apply {
            minimumHeight = 42.dp
            minimumWidth = 38.dp
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun showError(text: CharSequence?) {
        isErrorEnabled = !text.isNullOrBlank()
        error = text
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        if (!enabled) return
        findViewById<TextView>(R.id.textinput_error)?.apply {
            (parent as? ViewGroup)?.apply {
                this.layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    this.gravity = Gravity.CENTER
                }
            }
            this.gravity = Gravity.CENTER
        }
    }


    fun showCustomError(show: Boolean) {
        if (show) {
            endIconMode = END_ICON_CUSTOM
            setEndIconDrawable(R.drawable.ic_input_error_icon)
            setEndIconTintList(getColorStateList(R.color.title_text_error_red))
        } else {
            endIconDrawable = null
            endIconMode = END_ICON_NONE
        }
    }

    fun showIconError(show: Boolean){
        if (oldIconDrawable == null) oldIconDrawable = endIconDrawable
        if (oldIconMode == null) oldIconMode = endIconMode

        if (show) {
            endIconMode = END_ICON_CUSTOM
            setEndIconDrawable(R.drawable.ic_input_error_icon)
            setEndIconTintList(getColorStateList(R.color.title_text_error_red))
        } else {
            endIconMode = oldIconMode ?: END_ICON_NONE
            endIconDrawable = oldIconDrawable
            setEndIconTintList(getColorStateList(R.color.bottom_nav_item_selected_color))
        }
    }

    fun showLoadingIcon() {
        if (oldIconDrawable == null) oldIconDrawable = endIconDrawable
        endIconDrawable = getProgressBarDrawable()
    }


    fun hideLoadingIcon() {
        endIconDrawable = oldIconDrawable
    }

    private fun getProgressBarDrawable(): Drawable {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.progressBarStyleSmall, value, false)
        val progressBarStyle = value.data
        val attributes = intArrayOf(android.R.attr.indeterminateDrawable)
        val array = context.obtainStyledAttributes(progressBarStyle, attributes)
        val drawable = array.getDrawableOrThrow(0)
        array.recycle()

        (drawable as? Animatable)?.start()

        setEndIconTintList(ContextCompat.getColorStateList(context, R.color.main_brown_color_new))
        drawable.setTintList(ContextCompat.getColorStateList(context, R.color.main_brown_color_new))

        return getResizedDrawable(drawable)
    }


    private fun getResizedDrawable(drawable: Drawable): Drawable {
        return LayerDrawable(arrayOf(drawable)).also { it.setLayerSize(0, 25.dp, 25.dp) }
    }

}