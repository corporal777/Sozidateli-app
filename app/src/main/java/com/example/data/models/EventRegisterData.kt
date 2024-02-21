package com.example.data.models

data class EventRegisterData(
    val event: EventRegistration,
    var hasDraft: Boolean = false,
    var fieldsData: MutableList<EventRegisterFieldData<*>> = mutableListOf(),
    var draftFieldsData: MutableList<EventRegisterFieldData<*>> = mutableListOf(),
) {
    fun getSortedFields(): List<EventRegisterFieldData<*>> {
        return fieldsData.sortedBy { x -> x.field.id }
    }

    fun getSortedDraftFields(): List<EventRegisterFieldData<*>> {
        return draftFieldsData.sortedBy { x -> x.field.id }
    }


    fun addFields(fields: List<EventRegisterFieldData<*>>?) {
        if (!fields.isNullOrEmpty()) {
            this.fieldsData.addAll(fields)
        }
    }

    fun addField(field: EventRegisterFieldData<*>?) {
        if (field != null){
            this.fieldsData.add(field)
            this.draftFieldsData.add(field)
        }
    }

    fun addDraftFields(draft: List<EventRegisterFieldData<*>>?) {
        if (!draft.isNullOrEmpty()){
            this.draftFieldsData.addAll(draft)
        }
    }
}