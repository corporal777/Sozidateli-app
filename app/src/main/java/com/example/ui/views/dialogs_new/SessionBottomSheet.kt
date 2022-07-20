package com.example.ui.views.dialogs_new

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.BottomSheetCalendarBinding
import com.example.databinding.BottomSheetSessionBinding
import com.example.extensions.calendar
import com.example.extensions.dp
import com.example.ui.views.calendarView.CalendarDay
import com.example.ui.views.calendarView.DayViewDecorator
import com.example.ui.views.calendarView.DayViewFacade
import com.example.ui.views.calendarView.spans.DotSpan
import com.google.android.material.bottomsheet.BottomSheetDialog
import setOnClickListener
import java.util.*

class SessionBottomSheet(val context: Context, val session: UserSessionModel) {

    private val mBinding = BottomSheetSessionBinding.inflate(LayoutInflater.from(context))
    private var onActionClick: (id : Int) -> Unit = {}
    private var mDialog = BottomSheetDialog(context)

    init {
        mDialog.setContentView(mBinding.root)


        mBinding.apply {
            tvDevice.text = session.device
            tvIpAddress.text = session.ipAddress
            tvLocation.text = session.location
            decorDeviceIcon(ivDeviceIcon, session.device)
            cardAction.setOnClickListener {
                onActionClick(session.sessionId)
                mDialog.dismiss()
            }
        }

        mDialog.show()
        mBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }
    }


    private fun decorDeviceIcon(icon: ImageView, device: String) {
        if (device.contains("android", true)) {
            icon.setImageResource(R.drawable.ic_android_device)
        } else if (device.contains("ios", true)) {
            icon.setImageResource(R.drawable.ic_apple_device)
        } else {
            icon.setImageResource(R.drawable.ic_desktop_device)
        }

    }

    fun setSelectCallback(block: (id : Int) -> Unit): SessionBottomSheet {
        onActionClick = block
        return this
    }


}