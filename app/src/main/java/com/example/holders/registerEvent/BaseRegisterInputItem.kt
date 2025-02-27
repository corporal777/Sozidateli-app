package com.example.holders.registerEvent

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.app.R
import com.example.data.models.eventRegister.EventRegisterField
import com.example.util.getColorStateList

abstract class BaseRegisterInputItem<T : ViewDataBinding>(
    fieldData: EventRegisterField<String>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterItem<T>(fieldData, onDataChange){


    abstract fun getInputView(binding: T): View
    abstract fun getErrorFrameView(binding: T): View

    override fun bind(viewBinding: T, position: Int) {
        super.bind(viewBinding, position)
        getInputView(viewBinding).apply {
            backgroundTintList = if (field.isRequired)
                getColorStateList(R.color.background_input_event_register_required)
            else getColorStateList(R.color.background_input_event_register)
        }
    }

    override fun showError(show: Boolean) {
        super.showError(show)
        if (binding != null) getErrorFrameView(binding!!).isVisible = show
    }

    override fun showErrorText(show: Boolean, errorText: String) {
        super.showErrorText(show, errorText)
        if (binding != null) getErrorFrameView(binding!!).isVisible = show
    }
}