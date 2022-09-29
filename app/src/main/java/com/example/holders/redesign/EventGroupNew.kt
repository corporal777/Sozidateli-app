package com.example.holders.redesign

import com.example.data.models.*
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventGroupNew(
    data : EventNew,
    eventClickListener: EventItemNew.OnEventClickListener,
) : NestedGroup() {
    private val eventItem = EventItemNew(
        data,
        data.id.toString(),
        data.state,
        data.status?.value,
        data.binds?.currentUserRegistration?.status?.value,
        data.binds?.organization?.backgroundColor?.value,
        data.image?.uri,
        data.binds?.eventRegistrationState,
        data.userAgreement?.uri,
        data.binds?.currentUserRegistration?.id.toString(),
        data.name,
        data.address?.getShortAddress(),
        data.holdingDate?.from,
        data.holdingDate?.to,
        eventClickListener,
    )

    init {
        eventItem.registerGroupDataObserver(this)
        //dataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> eventItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            eventItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    fun updateButtonState(event : EventNew?){
        eventItem.notifyChanged(event)
    }

    override fun getGroupCount() = 1
}