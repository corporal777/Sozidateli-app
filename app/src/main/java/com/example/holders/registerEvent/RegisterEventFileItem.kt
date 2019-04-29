package com.example.holders.registerEvent

import android.text.InputType
import android.view.MotionEvent
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.data.models.user.RecommendationFiles
import com.example.ui.request.RequestPresenter
import com.example.util.ID
import com.example.util.NAME
import com.google.gson.internal.LinkedTreeMap
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*
import java.io.File


open class RegisterEventFileItem(private val fieldRegister:RegisterEventField, private val presenter: RequestPresenter) : BaseRegisterItem(presenter) {

   private var file: File?=null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            etInput.inputType = InputType.TYPE_NULL
            etInput.hint = fieldRegister.name
            file?.let {
                etInput.setText(it.name)
            }
            etInput.setOnTouchListener { view, motionEvent ->
                if(motionEvent.action == MotionEvent.ACTION_UP){
                    presenter.onClickOpenFileSelector(position)
                }

                return@setOnTouchListener true
            }

            if (isFirstBind) {
                fieldRegister.dataFromServer?.let {
                    it.value?.let { data->
                        if(data is LinkedTreeMap<*,*>){
                            etInput.setText(data[NAME].toString())
                            onDataChange(fieldRegister.field_id, data[ID])
                        }
                    }
                }
                isFirstBind = false
            }
        }
    }

    fun updateFile(file:File){
        this.file = file
        onDataChange(fieldRegister.field_id,file)
        notifyChanged()
    }

    override fun getLayout() = R.layout.register_event_input
}