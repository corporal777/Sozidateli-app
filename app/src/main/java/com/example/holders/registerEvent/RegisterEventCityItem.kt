package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.register_event_city.view.*

open class RegisterEventCityItem(
        private val fieldRegister: RegisterEventField,
        presenter: RequestPresenter
) : BaseRegisterItem(presenter) {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {

        viewHolder.itemView.etInput.apply {
            hint = fieldRegister.name
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

//            onItemSelected = {
//                onDataChange(fieldRegister.field_id, it.unrestricted_value)
//            }

            if (isFirstBind) {
                fieldRegister.dataFromServer?.let {
                    if (it.value is String) {
                        setTextWithoutSearch(it.value.toString())
                    }
                }
                isFirstBind = false
            }
        }
    }

    override fun getLayout() = R.layout.register_event_city
}