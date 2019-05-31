package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_city.view.*

open class RegisterEventCityItem(private val fieldRegister:RegisterEventField, presenter: RequestPresenter) : BaseRegisterItem(presenter) {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.etInput.apply {
            hint = fieldRegister.name
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

            onItemSelected = {
                onDataChange(fieldRegister.field_id,it.unrestricted_value)
            }

            if (isFirstBind) {
                fieldRegister.dataFromServer?.let {
                    if (it.value is String) {
                        setTextWithoutListen(it.value.toString())
                    }
                }
                isFirstBind = false
            }
        }
    }

    override fun getLayout() = R.layout.register_event_city
}