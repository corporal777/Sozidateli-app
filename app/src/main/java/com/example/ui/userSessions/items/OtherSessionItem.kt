package com.example.ui.userSessions.items

import android.widget.ImageView
import com.example.R
import com.example.data.models.UserSessionModel
import com.example.databinding.ItemOtherSessionBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.xwray.groupie.databinding.BindableItem
import java.util.*

class OtherSessionItem(
    val session: UserSessionModel?,
    val onSessionClick: (session: UserSessionModel) -> Unit
) : BindableItem<ItemOtherSessionBinding>(session?.sessionId ?: 0) {


    private var deviceName = ""
    private var deviceType = ""
    private var deviceLocation = ""

    init {
        if (session != null) {
            deviceName =
                if (session.deviceModel.isNullOrEmpty() || session.deviceModel == "Device not defined") {
                    "Устройство не определено"
                } else {
                    session.deviceModel
                }
            deviceType = if (session.device.isNullOrEmpty()) {
                if (session.deviceModel.contains("iphone", true)) {
                    "Созидатели iOS, IP " + session.ipAddress
                } else {
                    "Устройство не определено"
                }
            } else {
                session.device + ", IP " + session.ipAddress
            }

            deviceLocation =
                if (session.location.isNullOrEmpty() || session.location.contains("Location not defined")) {
                    "Местоположение не определено"
                } else {
                    session.location
                }
        }
    }

    override fun bind(viewBinding: ItemOtherSessionBinding, position: Int) {
        viewBinding.apply {
            tvDeviceName.text = deviceName
            tvDeviceType.text = deviceType
            //tvLocation.text = deviceLocation + " • " + getSessionStatus()
            tvLocation.text = deviceLocation + " • " + getSessionDate()

            if (session != null)
                decorDeviceIcon(ivDeviceIcon, session)

            itemContainer.setOnClickListener {
                onSessionClick(session!!)
            }
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

    private fun getSessionStatus(): String {
        val calToday = System.currentTimeMillis().calendar()
        val calSession = if (!session?.sessionEnd.isNullOrEmpty())
            defaultServerDateFormatter.parse(session?.sessionEnd).time.calendar()
        else {
            defaultServerDateFormatter.parse(session?.sessionStart).time.calendar()
        }
        val sessionDay = calSession.get(Calendar.DAY_OF_MONTH)
        val today = calToday.get(Calendar.DAY_OF_MONTH)
        val status = if (today - sessionDay == 1) {
            "вчера"
        } else if (today == sessionDay) {
            "сегодня"
        } else {
            calSession.get(Calendar.DAY_OF_MONTH)
                .toString() + "." + calSession.get(Calendar.MONTH) + "." + calSession.get(
                Calendar.YEAR
            )
        }

        return status
    }

    private fun getSessionDate(): String {
        //val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")
//        val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
//        val today = LocalDateTime.now()
//        val sessionDate = if (!session?.sessionEnd.isNullOrEmpty()){
//            LocalDateTime.parse(session?.sessionEnd, format)
//        } else if (!session?.sessionStart.isNullOrEmpty()){
//            LocalDateTime.parse(session?.sessionStart, format)
//        } else today
//        val status = if (today.dayOfMonth - sessionDate.dayOfMonth == 1){
//            "вчера"
//        } else if (today.dayOfMonth == sessionDate.dayOfMonth && today.monthValue == sessionDate.monthValue){
//            "сегодня"
//        } else {
//            sessionDate.dayOfMonth.toString() + "." + sessionDate.monthValue.toString() + "." + sessionDate.year
//        }

//        return status
        return ""
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is OtherSessionItem) return false
        if (session != other.session) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_other_session
}