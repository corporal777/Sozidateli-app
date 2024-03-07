package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import coil.load
import coil.request.CachePolicy
import coil.size.Scale
import com.example.R
import com.example.extensions.dp
import com.example.extensions.px
import com.example.util.getColor
import com.example.util.getDrawable
import io.github.inflationx.calligraphy3.CalligraphyUtils


class CustomAvatarView : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val avatarView = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
    private val backgroundView = View(context).apply {
        isVisible = false
        this.setBackgroundColor(getColor(R.color.profile_id_text))
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
    private val textView = TextView(context).apply {
        isVisible = false
        CalligraphyUtils.applyFontToTextView(context, this, "fonts/sf_pro_text_semibold.ttf")
        letterSpacing = -0.01f
        text = "Нет фото"
        setTextColor(getColor(R.color.white))
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            addRule(CENTER_IN_PARENT)
        }
    }
    private var textViewSize = 0f

    init {
        clipToOutline = true
        background = getDrawable(R.drawable.background_corners)

        removeAllViews()
        addView(avatarView)
        addView(backgroundView)
        addView(textView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (textViewSize <= 0f && width != 0) {
            textViewSize = if (width.px > 100) 14f else 12f
            textView.textSize = textViewSize
        }
    }

    fun setImage(uri: String?, isDefault: Boolean) {
        backgroundView.isVisible = isDefault
        textView.isVisible = isDefault
        avatarView.apply {
            load(uri) {
                crossfade(200)
                placeholder(R.drawable.background_image_placeholder)
                error(R.drawable.avatar_placeholder_rectangle)
                scale(Scale.FILL)
                diskCachePolicy(CachePolicy.ENABLED)

                listener(
                    onStart = {},
                    onCancel = {},
                    onError = { _, _ -> },
                    onSuccess = { r, m -> }
                )
            }
        }
    }

}