package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
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
        setPadding(5.dp, 0, 5.dp, 0)
        layoutParams = LinearLayout.LayoutParams(32.dp, 32.dp)
        background = ContextCompat.getDrawable(context, R.drawable.custom_toolbar_btn_background_selectable)
        minimumHeight = 32.dp
    }

    fun setAction(action: UserSubscribeButton.Action) {
        when (action) {
            UserSubscribeButton.Action.FAVORITE -> setActionFavorite()
            UserSubscribeButton.Action.UNFAVORITE -> setActionUnFavorite()
            else -> {}
        }
    }

    private fun setActionFavorite(){
        setImageResource(iconUnFavorite)
    }

    private fun setActionUnFavorite(){
        setImageResource(iconFavorite)
    }

    fun setActionAlternative(isFavorite : Boolean){
        if (isFavorite) setImageResource(iconUnFavorite)
        else setImageResource(iconFavorite)
    }

    fun setAlphaVision(enabled: Boolean){
        this.apply {
            isEnabled = enabled
            alpha = if (enabled) 1f
            else 0.6f
        }
    }

    fun setButtonMargins(top : Int, bottom : Int, left : Int, right : Int){
        val marginParams = ViewGroup.MarginLayoutParams(layoutParams)
        marginParams.setMargins(left, top, right, bottom)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(marginParams)
        setLayoutParams(layoutParams)
    }
}