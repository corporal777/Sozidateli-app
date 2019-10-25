package com.example.holders.registerEvent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.R
import com.example.data.models.RegisterEventFieldData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_register_event_radio.*

open class RegisterEventRadioBoxItem(
        private val fieldData: RegisterEventFieldData<String>
) : BaseRegisterItem(fieldData) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val values = field.values ?: emptyList()
        viewHolder.apply {
            radioGroup.apply {
                removeAllViews()
                values.forEachIndexed { index, value ->
                    addView((LayoutInflater.from(context).inflate(R.layout.item_radio_button, this, false) as RadioButton).apply {
                        id = index
                        text = value

                        fieldData.value?.let {
                            if (it == value) isChecked = true
                        }
                    }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                }

                setOnCheckedChangeListener { _, id -> fieldData.value = values[id] }
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_radio
}