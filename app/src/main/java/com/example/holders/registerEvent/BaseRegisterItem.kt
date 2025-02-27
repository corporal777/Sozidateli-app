package com.example.holders.registerEvent

import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.app.R
import com.example.data.models.eventRegister.EventRegisterField
import com.example.util.getColor
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
                if (show) setTextColor(getColor(R.color.red_new))
                else setTextColor(getColor(R.color.profile_data_text_hint))
            }
        }
    }

    open fun showErrorText(show: Boolean, errorText: String){
        if (binding != null) {
            getTitleView(binding!!).apply {
                if (show) {
                    text = errorText
                    setTextColor(getColor(R.color.red_new))
                }
                else {
                    text = fieldData.field.name
                    setTextColor(getColor(R.color.profile_data_text_hint))
                }
            }
        }
    }
}