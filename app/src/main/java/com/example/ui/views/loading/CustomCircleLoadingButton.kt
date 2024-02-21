package com.example.ui.views.loading

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCircleLoadingButtonBinding
import com.example.databinding.LayoutLoadingButtonBinding
import com.example.extensions.dp
import com.example.util.getColor
import com.example.util.getDrawable
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

    private var isProgressVisible = false

    private val loadingView =
        LayoutCircleLoadingButtonBinding.inflate(LayoutInflater.from(context), this, true)


    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleLoadingButton)
        val iconDrawable = a.getResourceId(R.styleable.CustomCircleLoadingButton_circleLoadingButtonDrawable, 0)
        val buttonText = a.getText(R.styleable.CustomCircleLoadingButton_circleLoadingButtonText)
        a.recycle()

        loadingView.btnAction.setLeftIcon(iconDrawable)
        loadingView.btnAction.text = buttonText
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
                background = getDrawable(R.drawable.custom_btn_rounded_corners_active_selectable)
                setTextColor(getColor(R.color.main_brown_color_new))
                setLeftIcon(R.drawable.ic_circle_plus_icon)
            } else {
                background = getDrawable(R.drawable.custom_btn_rounded_corners_inactive_selectable)
                setTextColor(getColor(R.color.circle_rounded_corners_button_color))
                setLeftIcon(R.drawable.ic_circle_done_icon)
            }
            text = str
        }
    }

    fun setButtonText(buttonText: CharSequence) {
        loadingView.btnAction.text = buttonText
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