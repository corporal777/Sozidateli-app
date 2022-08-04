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
import com.example.util.getDeviceId
import com.google.android.material.bottomsheet.BottomSheetDialog
import setOnClickListener
import java.util.*

class SessionBottomSheet(
    val onActionClick: () -> Unit,
    val context: Context,
    val session: UserSessionModel
) {

    private val mBinding = BottomSheetSessionBinding.inflate(LayoutInflater.from(context))

    private var mDialog = BottomSheetDialog(context)

    private var deviceName = ""
    private var deviceIp = ""
    private var deviceLocation = ""

    init {
        if (session != null) {
            deviceIp = if (!session.ipAddress.isNullOrEmpty()) {
                session.ipAddress
            } else {
                "IP адрес не определен"
            }
            deviceLocation =
                if (session.location.isNullOrEmpty() || session.location.contains("Location not defined")) {
                    "Не определено"
                } else {
                    session.location
                }

            deviceName = if (!session.device.isNullOrEmpty()) {
                if (session.device.contains("iOS", true) || session.device.contains("android", true)
                ) {
                    if (!session.appVersion.isNullOrEmpty()) {
                        session.device + " " + session.appVersion + " (" + session.appBuild + ")"
                    } else {
                        session.device
                    }
                } else {
                    session.device
                }
            } else {
                if (session.deviceModel.contains("iphone", true)) {
                    "Созидатели iOS"
                } else {
                    "Устройство не определено"
                }
            }


        }

        mDialog.setContentView(mBinding.root)


        mBinding.apply {
            if (deviceName.contains("iOS", true) || deviceName.contains("android", true)) {
                tvDeviceType.text = context.getString(R.string.app_label)
            } else {
                if (session.deviceModel.contains("iphone", true)) {
                    tvDeviceType.text = context.getString(R.string.app_label)
                } else {
                    tvDeviceType.text = context.getString(R.string.browser_label)
                }
            }
            tvDevice.text = deviceName
            tvIpAddress.text = deviceIp
            tvLocation.text = deviceLocation

            decorDeviceIcon(ivDeviceIcon, session)
            if (getDeviceId(context) == session.deviceId) {
                tvActionKill.text = context.getString(R.string.kill_all_other_sessions)
            } else {
                tvActionKill.text = context.getString(R.string.kill_session_label)
            }

            cardAction.setOnClickListener {
                onActionClick.invoke()
                //onActionKillDeviceSession(session)
                mDialog.dismiss()
            }
        }

        mDialog.show()
        mBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }
    }


    private fun decorDeviceIcon(icon: ImageView, session: UserSessionModel) {
        if (session.device.isNullOrEmpty()) {
            if (session.deviceModel.contains("iphone", true)) {
                icon.setImageResource(R.drawable.ic_apple_device)
            } else {
                icon.setImageResource(R.drawable.ic_desktop_device)
            }
        } else {
            if (session.device.contains("android", true)) {
                icon.setImageResource(R.drawable.ic_android_device)
            } else if (session.device.contains("ios", true)) {
                icon.setImageResource(R.drawable.ic_apple_device)
            } else {
                icon.setImageResource(R.drawable.ic_desktop_device)
            }
        }
    }

//    fun onKillDeviceSession(block: (session: UserSessionModel) -> Unit): SessionBottomSheet {
//        //onActionKillDeviceSession = block
//        return this
//    }
}