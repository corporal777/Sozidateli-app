package com.example.holders

import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.EventFormat
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.kotlinandroidextensions.Item

class EventGroup(
        eventId: String,
        status: Event.Status?,
        userRegistration: Event.RegistrationStatus?,
        backgroundColor: String?,
        logo: String?,
        format: EventFormat?,
        organizationEmails: List<EmailAffiliation>?,
        conferenceRegistrationClosed: Boolean,
        eventClickListener: EventStatusItem.OnEventClickListener,
        private val dataItem: Item
) : NestedGroup() {

    private val eventStatusItem = EventStatusItem(
            eventId.toLong(),
            eventId,
            status,
            userRegistration,
            backgroundColor,
            logo,
            format,
            organizationEmails,
            conferenceRegistrationClosed,
            eventClickListener
    )

    init {
        eventStatusItem.registerGroupDataObserver(this)
        dataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> eventStatusItem
            1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            eventStatusItem -> 0
            dataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2
}