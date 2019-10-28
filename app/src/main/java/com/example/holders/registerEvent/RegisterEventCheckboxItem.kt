package com.example.holders.registerEvent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.example.R
import com.example.data.models.RegisterEventFieldData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_register_event_checkbox.*

open class RegisterEventCheckboxItem(
        private val fieldData: RegisterEventFieldData<Set<String>>,
        onDataChange: (fieldData: RegisterEventFieldData<*>) -> Unit
) : BaseRegisterItem(fieldData, onDataChange) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val values = field.values ?: emptyList()
        viewHolder.apply {
            checkGroup.apply {
                removeAllViews()
                values.forEachIndexed { index, value ->
                    addView((LayoutInflater.from(context).inflate(R.layout.item_checkbox, this, false) as CheckBox).apply {
                        id = index
                        text = value

                        setOnCheckedChangeListener { _, isChecked ->
                            val valuesData = if (fieldData.value == null) {
                                val set = mutableSetOf<String>()
                                fieldData.value = set
                                set
                            } else {
                                fieldData.value as MutableSet<String>
                            }

                            if (isChecked) valuesData.add(value)
                            else valuesData.remove(value)
                            onDataChange()
                        }

                        fieldData.value?.let {
                            if (it.contains(value)) isChecked = true
                        }
                    }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_checkbox
}