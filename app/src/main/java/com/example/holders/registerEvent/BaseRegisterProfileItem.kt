package com.example.holders.registerEvent

import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.util.getColor
import com.xwray.groupie.databinding.BindableItem

abstract class BaseRegisterProfileItem<T : ViewDataBinding>(itemId: Long) : BindableItem<T>(itemId) {

    var isErrorShown: Boolean = false

    abstract fun getTitleView(binding: T): TextView?
    abstract fun getErrorFrameView(binding: T): View?

    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        getTitleView(viewBinding)?.apply {
            if (isErrorShown) setTextColor(getColor(R.color.red_new))
            else setTextColor(getColor(R.color.profile_data_text_hint))
        }
        getErrorFrameView(viewBinding)?.isVisible = isErrorShown
    }
}