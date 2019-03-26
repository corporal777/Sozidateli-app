package com.example.holders.registerEvent

import android.text.InputType
import android.view.MotionEvent
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.example.util.Utils
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.DateFormat
import java.util.*


open class RegisterEventDateItem(private val fieldRegister:RegisterEventField,
                                 private val isNeedTime:Boolean,
                                 presenter: RequestPresenter,
                                 private val fragmentManager: FragmentManager) : BaseRegisterItem(presenter), DatePickerDialog.OnDateSetListener,
                                                                                 TimePickerDialog.OnTimeSetListener {


    private var calendar = Calendar.getInstance()
    private val datePicker = DatePickerDialog.newInstance(
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
    )

    private val timePicker = TimePickerDialog.newInstance(
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
    )

    private var text = ""

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            etInput.inputType = InputType.TYPE_NULL
            etInput.hint = fieldRegister.name
            etInput.setText(text)

            etInput.setOnTouchListener { view, motionEvent ->
                if(motionEvent.action == MotionEvent.ACTION_UP){
                    if(!datePicker.isAdded) {
                        datePicker.show(fragmentManager, "date")
                    }
                }
                return@setOnTouchListener true
            }

            timePicker.setOnCancelListener {
                changeDateTime(Utils.defaultServerDateFormatterWithTime)
            }
            if(isFirstBind){
                fieldRegister.dataFromServer?.let {
                    val data = parseField(it,EventRegisterResponseField::class.java)
                    if(data.value is String) {
                        etInput.setText(data.value as String)
                        onDataChange(fieldRegister.field_id,data.value as String)
                    }
                }
                isFirstBind = false
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar.set(year,monthOfYear,dayOfMonth)

        if(isNeedTime){
            timePicker.show(fragmentManager,"time")
        } else{
            changeDateTime(Utils.defaultServerDateFormatter)
        }
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND, second)
        changeDateTime(Utils.defaultServerDateFormatterWithTime)
    }


    private fun changeDateTime(formatter: DateFormat){
        val date = formatter.format(calendar.timeInMillis)
        text = date
        onDataChange(fieldRegister.field_id,date)
        notifyChanged()
    }

    override fun getLayout() = R.layout.register_event_input
}