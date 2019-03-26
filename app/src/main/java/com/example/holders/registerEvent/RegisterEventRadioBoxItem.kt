package com.example.holders.registerEvent

import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_with_conteiner_item.view.*

open class RegisterEventRadioBoxItem(private val fieldRegister: RegisterEventField, presenter: RequestPresenter) : BaseRegisterItem(presenter) {

    private var selected: String? = null


    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (isFirstBind) {
            fieldRegister.dataFromServer?.let {
                val data = parseField(it, EventRegisterResponseField::class.java)

                if (data.value is String) {
                    selected = data.value as String
                }

            }
            isFirstBind = false
        }

        viewHolder.itemView.apply {
            tvTitle.text = fieldRegister.name
            fieldRegister.values?.let {
                llContainer.removeAllViews()

                val radioGroup = RadioGroup(context)
                llContainer.addView(radioGroup)
                it.forEach { value ->
                    val radioButton = RadioButton(context)
                    radioButton.id = it.indexOf(value)
                    radioButton.text = value
                    selected?.let {
                        if(selected == value){
                            radioButton.isChecked = true
                            onDataChange(fieldRegister.field_id, value)
                        }
                    }
                    radioGroup.addView(radioButton)
                }

                radioGroup.setOnCheckedChangeListener { radioGroup, id ->
                    val text = radioGroup.findViewById<RadioButton>(id).text
                    onDataChange(fieldRegister.field_id, text)
                }
            }


        }
    }

    override fun getLayout() = R.layout.register_event_with_conteiner_item
}