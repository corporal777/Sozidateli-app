package com.example.data.models

import android.util.Log

data class EventRegisterData(
    val event: EventRegistration,
    var hasDraft: Boolean = false,
    var fieldsData: MutableList<EventRegisterFieldData<*>> = mutableListOf(),
    var draftFieldsData: MutableList<EventRegisterFieldData<*>> = mutableListOf(),
) {
    fun getSortedFields(): List<EventRegisterFieldData<*>> {
        //return fieldsData.sortedBy { x -> x.field.id }
        return fieldsData
    }

    fun getDraftFields(): List<EventRegisterFieldData<*>> {
        //return draftFieldsData.sortedBy { x -> x.field.id }
        return draftFieldsData
    }


    fun addFields(fields: List<EventRegisterFieldData<*>>?): EventRegisterData {
        if (!fields.isNullOrEmpty()) this.fieldsData.addAll(fields)
        return this
    }

    fun addDraftFields(draft: List<EventRegisterFieldData<*>>?) {
        if (!draft.isNullOrEmpty()) {
            draftFieldsData.addAll(draft)
            val prefilled = fieldsData.find { x -> x is EventRegisterFieldData.Prefilled }
            if (prefilled != null) {
                draftFieldsData.find { x -> x is EventRegisterFieldData.Prefilled }.apply {
                    if (this is EventRegisterFieldData.Prefilled)
                        value = prefilled.value as EventRegisterPrefilledFields?
                }
            }
        }
    }

    companion object {
        fun init(event: EventNew): EventRegisterData {
            return EventRegisterData(EventRegistration.setEventRegistration(event))
        }
    }

}