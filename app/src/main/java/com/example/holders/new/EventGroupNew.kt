package com.example.holders.new

import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.example.data.models.EventPhoneModel
import com.example.data.models.EventRegistrationStateModel
import com.example.holders.EventStatusItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventGroupNew(
    eventId: String,
    status: Event.Status?,
    userRegistration: Event.Status?,
    backgroundColor: String?,
    logo: String?,
    format: EventFormat?,
    organizationEmails: List<EventPhoneModel>?,
    conferenceRegistrationClosed: Boolean,
    eventClickListener: EventStatusItem.OnEventClickListener,
    //private val dataItem: Item,
    userAgreement: String?,
    eventRegistrationState: EventRegistrationStateModel?,
    canShowActionButton: Boolean = true,
    registrationId: String? = null,
    name: String?,
    address: String?,
    conferenceStart: String?,
    conferenceActionStart: String?,
    date: String?

) : NestedGroup() {
    private val eventStatusItem = EventItemNew(
        eventId.toLong(),
        eventId,
        status,
        userRegistration,
        backgroundColor,
        logo,
        format,
        organizationEmails,
        conferenceRegistrationClosed,
        eventClickListener,
        userAgreement,
        canShowActionButton,
        eventRegistrationState,
        registrationId,
        name,
        address,
        date ?: ""
    )

    init {
        eventStatusItem.registerGroupDataObserver(this)
        //dataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> eventStatusItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            eventStatusItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 1
}