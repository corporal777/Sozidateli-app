package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*

open class RegisterEventStringItem(private val fieldRegister:RegisterEventField,presenter: RequestPresenter) : BaseRegisterItem(presenter) {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.etInput.apply {
            hint = fieldRegister.name
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE

            addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable {
                onDataChange(fieldRegister.field_id,if(it.isNullOrEmpty()) null else it.toString())
            })

            if (isFirstBind) {
                fieldRegister.dataFromServer?.let {
                    val data = parseField(it, EventRegisterResponseField::class.java)

                    if (data.value is String || data.value is Number) {
                        val text:String
                        if(data.value is Number){
                            text = (data.value as Number).toInt().toString()
                        } else{
                            text = data.value.toString()
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