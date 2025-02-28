package com.example.data.models

import com.example.data.models.eventRegister.EventRegisterField

data class EventRegisterData(
    val event: EventRegistration,
    var hasDraft: Boolean = false,
    var fieldsData: MutableList<EventRegisterField<*>> = mutableListOf(),
    var draftFieldsData: MutableList<EventRegisterField<*>> = mutableListOf(),
) {

    fun getFormId() = event.form?.id ?: 0

    fun getSortedFields(): List<EventRegisterField<*>> {
        //return fieldsData.sortedBy { x -> x.field.id }
        return fieldsData
    }

    fun getDraftFields(): List<EventRegisterField<*>> {
        //return draftFieldsData.sortedBy { x -> x.field.id }
        return draftFieldsData
    }


    fun addFields(fields: List<EventRegisterField<*>>?): EventRegisterData {
        if (!fields.isNullOrEmpty()) fieldsData.addAll(fields)
        return this
    }

    fun addDraftFields(draft: List<EventRegisterField<*>>?) {
        if (!draft.isNullOrEmpty()) {
            draftFieldsData.addAll(draft)
            val prefilled = fieldsData.find { x -> x is EventRegisterField.Prefilled }
            if (prefilled != null) {
                draftFieldsData.find { x -> x is EventRegisterField.Prefilled }.apply {
                    if (this is EventRegisterField.Prefilled)
                        value = prefilled.value as EventRegisterPrefilledFields?
                }
            }
        }
    }
}