package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*

open class RegisterEventStringItem(private val fieldRegister:RegisterEventField,presenter: RequestPresenter) : BaseRegisterItem(presenter) {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {

        viewHolder.itemView.etInput.apply {
            hint = fieldRegister.name
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE

            addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable {
                onDataChange(fieldRegister.field_id,if(it.isNullOrEmpty()) null else it.toString())
            })

            if (isFirstBind) {
                fieldRegister.dataFromServer?.let {
                    if (it.value is String || it.value is Number) {
                        val text:String
                        if(it.value is Number){
                            text = (it.value as Number).toInt().toString()
                        } else{
                            text = it.value.toString()
                        }
                        setText(text)
                    }

                }
                isFirstBind = false
            }
        }
    }

    override fun getLayout() = R.layout.register_event_input
}