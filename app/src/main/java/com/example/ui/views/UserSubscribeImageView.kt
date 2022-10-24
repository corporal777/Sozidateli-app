package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.extensions.dp

class UserSubscribeImageView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val iconFavorite = R.drawable.ic_star_filled
    private val iconUnFavorite = R.drawable.ic_star

    init {
        setPadding(8.dp, 8.dp, 8.dp, 8.dp)
        background = ContextCompat.getDrawable(context, R.drawable.custom_toolbar_btn_background_selectable)
    }

    fun setAction(action: UserSubscribeButton.Action) {
        when (action) {
            UserSubscribeButton.Action.FAVORITE -> setActionFavorite()
            UserSubscribeButton.Action.UNFAVORITE -> setActionUnFavorite()
        }
    }

    private fun setActionFavorite(){
        setImageResource(iconUnFavorite)
    }

    private fun setActionUnFavorite(){
        setImageResource(iconFavorite)
    }

    fun setActionAlternative(isFavorite : Boolean){
        if (isFavorite) {
            setImageResource(iconUnFavorite)
        } else {
            setImageResource(iconFavorite)
        }
    }

    fun setAlphaVision(enabled: Boolean){
        this.apply {
            isEnabled = enabled
            alpha = if (enabled) 1f
            else 0.6f
        }
    }
}