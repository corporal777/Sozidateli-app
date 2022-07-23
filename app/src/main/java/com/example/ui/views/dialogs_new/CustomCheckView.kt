package com.example.ui.views.dialogs_new

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.setPadding
import com.example.R
import com.example.extensions.dp
import setSelectableItemBackgroundBorderless

class CustomCheckView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setBackground()
    }


    private fun setBackground() {
        setImageResource(R.drawable.ic_switch)
    }

    fun setImage(state: Boolean) {
        if (state) {
            setImageResource(R.drawable.ic_switch_enabled)
        } else {
            setImageResource(R.drawable.ic_switch)
        }
    }

}