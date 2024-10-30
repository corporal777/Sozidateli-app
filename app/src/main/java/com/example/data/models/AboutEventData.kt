package com.example.data.models

data class AboutEventData(
    var event: EventNew,
    var speakers: List<MemberModel> = emptyList(),
    var showMoreSpeakers: Boolean = false,
    var tags: List<Tag> = emptyList(),
    var subEvents: List<EventActivityModel> = emptyList(),
    var partners: List<PartnerModel> = emptyList()
) {

    init {
        setTags()
        setSortedSpeakers()
        setSubEvents()
        setPartners()
    }

    fun getUserRegistrationState() =
        event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED

    private fun setTags() {
        this.tags =
            event.binds?.tag?.map { Tag.EventTag(it.id.toString(), it.name ?: "") } ?: emptyList()
    }

    private fun setSortedSpeakers() {
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

    private fun setSubEvents() {
        val list = event.binds?.activity?.sortedBy { it.holdingDate?.from }
        if (!list.isNullOrEmpty()) {
            if (list.size > 4) this.subEvents = list.subList(0, 4)
            else this.subEvents = list
        }
    }

    private fun setPartners() {
        this.partners = event.binds?.partner ?: emptyList()
    }

    fun getSelectedTags(): List<NewTags> {
        return tags.filter { x -> x.isSelected }.map {
            NewTags(it.id, it.name, it.isSelected)
        }
    }
}