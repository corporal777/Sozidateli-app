package com.example.holders.registerEvent

import android.util.TypedValue
import android.widget.CheckBox
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.google.gson.internal.LinkedTreeMap
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_with_conteiner_item.view.*

open class RegisterEventSelectBoxItem(private val fieldRegister:RegisterEventField, presenter: RequestPresenter) : BaseRegisterItem(presenter) {

    var selected = LinkedTreeMap<String,String>()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        if (isFirstBind) {
            fieldRegister.dataFromServer?.let {
                val data = parseField(it, EventRegisterResponseField::class.java)

                if (data.value is LinkedTreeMap<*,*>) {
                    selected = data.value as LinkedTreeMap<String,String>
                }

            }
            isFirstBind = false
        }

        viewHolder.itemView.apply {
            tvTitle.text = fieldRegister.name
            fieldRegister.values?.let {
                llContainer.removeAllViews()
                it.forEach {value->

                    val checkBox = CheckBox(context)
                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,15f)
                    checkBox.text = value
                    checkBox.id = it.indexOf(value)

                    if(selected.containsKey(checkBox.id.toString())){
                        checkBox.isChecked = true
                        onDataChange(fieldRegister.field_id,value,checkBox.id.toString())
                    }

                    llContainer.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                        onDataChange(fieldRegister.field_id,if(isChecked) compoundButton.text.toString() else null,compoundButton.id.toString())
                    }
                }
            }
        }
    }

    override fun getLayout() = R.layout.register_event_with_conteiner_item
}