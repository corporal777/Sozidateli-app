package com.example.holders.registerEvent

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.util.getColorStateList

abstract class BaseRegisterInputItem<T : ViewDataBinding>(
    fieldData: EventRegisterFieldData<String>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<T>(fieldData, onDataChange){


    abstract fun getInputView(binding: T): View
    abstract fun getErrorFrameView(binding: T): View

    override fun bind(viewBinding: T, position: Int) {
        super.bind(viewBinding, position)
        getInputView(viewBinding).apply {
            backgroundTintList = if (field.required)
                getColorStateList(R.color.background_input_event_register_required)
            else getColorStateList(R.color.background_input_event_register)
        }
    }

    override fun showError(show: Boolean) {
        super.showError(show)
        if (binding != null) getErrorFrameView(binding!!).isVisible = show
    }
}