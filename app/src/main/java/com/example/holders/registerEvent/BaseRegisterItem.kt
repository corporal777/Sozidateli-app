package com.example.holders.registerEvent

import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.app.R
import com.example.data.models.eventRegister.EventRegisterField
import com.example.common.extensions.textColor
import com.xwray.groupie.viewbinding.BindableItem

abstract class BaseRegisterItem<T : ViewDataBinding>(
    private val fieldData: EventRegisterField<*>,
    private val onDataChange: (fieldData: EventRegisterField<*>) -> Unit
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
                text = fieldData.field.name
                textColor = if (show) R.color.red_new else R.color.profile_data_text_hint
            }
        }
    }

    open fun showErrorText(show: Boolean, errorText: String){
        if (binding != null) {
            getTitleView(binding!!).apply {
                text = if (show) errorText else fieldData.field.name
                textColor = if (show) R.color.red_new else R.color.profile_data_text_hint
            }
        }
    }
}