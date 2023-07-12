package com.example.ui.notification.center.redesign.items

import android.content.Context
import com.example.data.models.Notification
import com.example.extensions.findItemBy
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class NotificationsItemsGroup(
    val context: Context,
    val date: String?,
    val list: List<Notification>,
    val listener: NotificationItemNew.OnNotificationActionListener,
) : NestedGroup() {

    private val dateItem = NotificationsDateItem(date)
    private val notificationsSection = Section()

    init {
        dateItem.registerGroupDataObserver(this)

        if (!list.isNullOrEmpty()){
            notificationsSection.update(
                list.map {
                    when (it.type) {
                        Notification.Type.SIMPLE -> SimpleNotificationItemNew(
                            context,
                            it,
                            listener
                        )
                        Notification.Type.ACCEPTABLE -> AcceptNotificationItemNew(
                            context,
                            it,
                            listener
                        )
                        Notification.Type.RATE -> RateNotificationItemNew(
                            context,
                            it,
                            listener
                        )
                    }
                }

            )
        }
        notificationsSection.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> dateItem
            1 -> notificationsSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            dateItem -> 0
            notificationsSection -> 1
            else -> -1
        }
    }

    fun updateNotification(data: Notification){
        val idLong = data.id.toLong()

        val item = notificationsSection.findItemBy<NotificationItemNew<*>> { x -> x.id == idLong }
        if (item != null){
            item.notifyChanged(data)
        }
    }

    fun isSameDate(newDate: String?) = newDate == date

    override fun getGroupCount() = 2
}