package com.examle.data.models

data class AboutEventData(
    var event: com.example.data.models.EventNew,
    var speakers: List<com.example.data.models.MemberModel> = emptyList(),
    var showMoreSpeakers: Boolean = false,
    var tags: List<com.example.data.models.Tag> = emptyList(),
    var subEvents: Map<String, List<com.example.data.models.EventActivityModel>> = emptyMap(),
    var partners: List<com.example.data.models.PartnerModel> = emptyList()
) {

    init {
        setTags()
        setSortedSpeakers()
        setSubEvents()
        setPartners()
    }

    fun getUserRegistrationState() =
        event.binds?.currentUserRegistration?.status?.value == com.example.data.models.Event.Status.APPROVED

    private fun setTags() {
        this.tags =
            event.binds?.tag?.map {
                com.example.data.models.Tag.EventTag(
                    it.id.toString(),
                    it.name ?: ""
                )
            } ?: emptyList()
    }

    private fun setSortedSpeakers() {
        val list = event.binds?.member?.filter { it.role == "speaker" }
        if (!list.isNullOrEmpty()) {
            val members = arrayListOf<com.example.data.models.MemberModel>()
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
            subEvents = list.apply {
                if (list.size > 4) subList(0, 3)
            }.groupBy { it.holdingDate?.from?.split(" ")?.get(0) ?: "" }
        }
    }

    private fun setPartners() {
        this.partners = event.binds?.partner ?: emptyList()
    }

    fun getSelectedTags(): List<com.example.data.models.NewTags> {
        return tags.filter { x -> x.isSelected }.map {
            com.example.data.models.NewTags(it.id, it.name, it.isSelected)
        }
    }
}