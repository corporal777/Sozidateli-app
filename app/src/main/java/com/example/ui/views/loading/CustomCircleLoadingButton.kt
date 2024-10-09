package com.example.ui.views.loading

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.LayoutCircleLoadingButtonBinding
import com.example.app.databinding.LayoutLoadingButtonBinding
import com.example.extensions.dp
import com.example.util.getColor
import com.example.util.getDrawable
import com.example.util.setLeftDrawableWithIntrinsicBounds
import io.github.inflationx.calligraphy3.CalligraphyUtils

class CustomCircleLoadingButton : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val loadingView = LayoutCircleLoadingButtonBinding.inflate(LayoutInflater.from(context), this, true)


    private var isProgressVisible = false
    var buttonText : CharSequence? = null
        set(value) {
            loadingView.btnAction.text = value
            field = value
        }
    var buttonDrawable : Int = 0
        set(value) {
            loadingView.btnAction.setButtonLeftIcon(value)
            field = value
        }


    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleLoadingButton)
        val drawable = a.getResourceId(R.styleable.CustomCircleLoadingButton_circleButtonDrawable, 0)
        val text = a.getText(R.styleable.CustomCircleLoadingButton_circleButtonText)
        a.recycle()

        buttonDrawable = drawable
        buttonText = text
    }

    init {
        loadingView.progressLoad.apply {
            isVisible = false
            setProgressColor(ContextCompat.getColor(context, R.color.main_brown_color_new))
            setSize(17.dp)
            setStroke(8f)
        }
        loadingView.btnAction.setOnClickListener {
            this.callOnClick()
        }
    }


    fun setActiveWithIcon(isActive : Boolean, str : String){
        loadingView.btnAction.apply {
            if (isActive) {
                background = getDrawable(R.drawable.btn_rounded_corners_active)
                setTextColor(getColor(R.color.main_brown_color_new))
                setButtonLeftIcon(R.drawable.ic_circle_plus_icon)
            } else {
                background = getDrawable(R.drawable.btn_rounded_corners_inactive)
                setTextColor(getColor(R.color.circle_rounded_corners_button_color))
                setButtonLeftIcon(R.drawable.ic_circle_done_icon)
            }
            text = str
        }
    }


    private fun Button.setButtonLeftIcon(icon: Int){
        if (icon != 0) {
            setLeftDrawableWithIntrinsicBounds(icon)
            compoundDrawablePadding = 5.dp
        }
    }


    fun showProgressLoading(show : Boolean) {
        if (isProgressVisible == show) return
        isProgressVisible = show
        loadingView.apply {
            progressLoad.isVisible = show
            btnAction.isInvisible = show

            this@CustomCircleLoadingButton.isClickable = !show
        }
    }
}