package com.example.holders.registerEvent

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.util.getColor
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class BaseRegisterItem<T : ViewDataBinding>(
    private val fieldData: EventRegisterFieldData<*>,
    private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BindableItem<T>(fieldData.field.id.toLong()) {

    protected val field = fieldData.field
    protected var binding: T? = null

    abstract fun getTitleView(binding: T): TextView

    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        binding = viewBinding
        getTitleView(viewBinding).apply {
            text = fieldData.field.name
            isVisible = !fieldData.field.name.isNullOrEmpty()
        }
    }

    protected fun onDataChange() {
        onDataChange(fieldData)
    }

    open fun showError(show: Boolean) {
        if (binding != null) {
            getTitleView(binding!!).apply {
                if (show) setTextColor(getColor(R.color.red_new))
                else setTextColor(getColor(R.color.profile_data_text_hint))
            }
        }
    }
}