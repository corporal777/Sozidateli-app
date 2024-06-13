package com.example.holders.registerEvent

import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.example.data.models.EventRegisterFieldData
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class BaseRegisterItem<T : ViewDataBinding>(
        private val fieldData: EventRegisterFieldData<*>,
        private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BindableItem<T>(fieldData.field.id.toLong()) {

    protected val field = fieldData.field

    abstract fun getTitleView(binding: T): TextView

    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        getTitleView(viewBinding).apply {
            text = fieldData.field.name
            isVisible = !fieldData.field.name.isNullOrEmpty()
        }
    }

    protected fun onDataChange() {
        onDataChange(fieldData)
    }
}