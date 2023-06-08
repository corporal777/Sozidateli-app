package com.example.ui.event.about.items

import com.example.data.models.*

data class AboutEventData(
    var event: EventNew,
    var speakers: List<MemberModel> = emptyList(),
    var showMoreSpeakers: Boolean = false,
    var tags: List<Tag> = emptyList(),
    var subEvents: Map<String, List<EventActivityModel>> = emptyMap(),
    var showMoreSubEvents: Boolean = false,
    var partners: List<PartnerModel> = emptyList()
) {

    fun getUserRegistrationState() =
        event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED

    fun setTags() {
        this.tags =
            event.binds?.tag?.map { Tag.EventTag(it.id.toString(), it.name ?: "") } ?: emptyList()
    }

    fun setSortedSpeakers() {
        val list = event.binds?.member?.filter { it.role == "speaker" }
        if (!list.isNullOrEmpty()) {
            val members = arrayListOf<MemberModel>()
            members.addAll(list.filter { x -> x.isLead == true }
                .sortedBy { x -> x.binds?.user?.fullName })
            members.addAll(list.filter { x -> x.isLead == false }
                .sortedBy { x -> x.binds?.user?.fullName })
            this.showMoreSpeakers = list.size > 5
            if (members.size > 5) speakers = members.subList(0, 5)
            else speakers = members
        }
    }

    fun setSubEvents() {
        val list = event.binds?.activity
        if (!list.isNullOrEmpty()) {
            this.showMoreSubEvents = list.size > 4
            if (list.size > 4) {
                this.subEvents = list.subList(0, 4).groupBy { event ->
                    event.holdingDate?.from?.split(" ")?.get(0) ?: ""
                }.toSortedMap()
            } else {
                this.subEvents = list.groupBy { event ->
                    event.holdingDate?.from?.split(" ")?.get(0) ?: ""
                }.toSortedMap()
            }
        }
    }

    fun setPartners() {
        this.partners = event.binds?.partner ?: emptyList()
    }
}