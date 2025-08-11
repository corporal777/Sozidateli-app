package com.examle.data.models.eventRegister

import com.examle.data.models.EventFormFieldModel

sealed class EventRegisterField<T>(val field: EventFormFieldModel, var value: T?) {

//    abstract fun isValid(): Boolean
//    abstract fun hasForm(): Boolean
//
//    class Prefilled(field: EventFormFieldModel, value: EventRegisterPrefilledFields?) :
//        EventRegisterField<EventRegisterPrefilledFields>(field, value) {
//        override fun isValid() = !field.isRequired || value?.isPrefilledFieldsValid() == true
//        override fun hasForm() = value != null
//    }
//
//    class Title(f: EventFormFieldModel, v: String?) : EventRegisterField<String>(f, v) {
//        override fun isValid() = true
//        override fun hasForm() = false
//    }
//
//    class Text(f: EventFormFieldModel, v: String?) : EventRegisterField<String>(f, v) {
//        override fun isValid(): Boolean {
//            return when (field.type) {
//                EventFormFieldModel.Type.EMAIL ->
//                    if (field.isRequired || !value.isNullOrEmpty()) isEmailValid(value) else true
//
//                EventFormFieldModel.Type.SITE ->
//                    if (field.isRequired || !value.isNullOrEmpty()) isValidSite(value) else true
//
//                else -> if (field.isRequired) !value.isNullOrEmpty() else true
//            }
//        }
//
//        override fun hasForm() = !value.isNullOrEmpty()
//    }
//
//    class Phone(f: EventFormFieldModel, v: String?) : EventRegisterField<String>(f, v) {
//        override fun isValid(): Boolean =
//            if (field.isRequired || !value.isNullOrEmpty()) isValidPhone(value) else true
//
//        override fun hasForm() = !value.isNullOrEmpty()
//    }
//
//    class Choice(f: EventFormFieldModel, v: Boolean?) : EventRegisterField<Boolean>(f, v) {
//        override fun isValid(): Boolean = if (field.isRequired) value == true else true
//        override fun hasForm() = value != null
//    }
//
//    class Passport(f: EventFormFieldModel, v: EventPassport?) : EventRegisterField<EventPassport>(f, v) {
//        override fun isValid(): Boolean {
//            val passport = value
//            return !field.isRequired || passport != null && passport.isDataComplete()
//        }
//
//        override fun hasForm(): Boolean {
//            val passport = value
//            return passport != null
//        }
//    }
//
//    class Date(f: EventFormFieldModel, v: String?) : EventRegisterField<String>(f, v) {
//        override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
//        override fun hasForm() = !value.isNullOrEmpty()
//    }
//
//    class SelectBox(f: EventFormFieldModel, v: String?) : EventRegisterField<String>(f, v) {
//        override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
//        override fun hasForm() = !value.isNullOrEmpty()
//    }
//
//    class RadioBox(f: EventFormFieldModel, v: String?) : EventRegisterField<String>(f, v) {
//        override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
//        override fun hasForm() = !value.isNullOrEmpty()
//    }
//
//    class Checkbox(f: EventFormFieldModel, v: Set<String>?) : EventRegisterField<Set<String>>(f, v) {
//        override fun isValid() = !field.isRequired || !value.isNullOrEmpty()
//        override fun hasForm() = !value.isNullOrEmpty()
//    }
//
//    class File(f: EventFormFieldModel, v: EventFile?) : EventRegisterField<EventFile?>(f, v) {
//        override fun isValid(): Boolean =
//            !field.isRequired || value != null && value?.name?.isNotEmpty() == true
//        override fun hasForm(): Boolean =
//            value != null && value?.name?.isNotEmpty() == true
//    }
//
//    companion object {
//        fun createFieldsData(
//            fields: List<EventFormFieldModel>?,
//            results: List<EventFormResultFieldModel?>?
//        ): List<EventRegisterField<*>>? {
//            return fields?.mapNotNull { field ->
//                val findValue: () -> JsonElement? = { results?.find { field.id == it?.id }?.value }
//
//                when (field.type) {
//                    EventFormFieldModel.Type.PREFILLED -> {
//                        val value = results?.find { field.id == it?.id }?.fields
//                        Prefilled(field, value.prefFromJson(field))
//                    }
//
//                    EventFormFieldModel.Type.SEPARATOR ->
//                        Title(field, findValue().fromJson<String>())
//
//                    EventFormFieldModel.Type.PHONE ->
//                        Phone(field, findValue().fromJson<String>())
//
//                    EventFormFieldModel.Type.STRING,
//                    EventFormFieldModel.Type.TEXT_AREA,
//                    EventFormFieldModel.Type.EMAIL,
//                    EventFormFieldModel.Type.SITE,
//                    EventFormFieldModel.Type.NUMBER ->
//                        Text(field, findValue().fromJson<String>())
//
//                    EventFormFieldModel.Type.DATE,
//                    EventFormFieldModel.Type.DATETIME,
//                    EventFormFieldModel.Type.DATETIMEPLANED ->
//                        Date(field, findValue().fromJson<String>())
//
//                    EventFormFieldModel.Type.CHECKBOXES,
//                    EventFormFieldModel.Type.CHECKBOX ->
//                        Checkbox(field, findValue().fromJson<Set<String>>())
//
//                    EventFormFieldModel.Type.SELECT_BOX ->
//                        SelectBox(field, findValue().fromJson<String>())
//
//                    EventFormFieldModel.Type.RADIO_BOX ->
//                        RadioBox(field, findValue().fromJson<String>())
//
//                    EventFormFieldModel.Type.FILE ->
//                        File(field, findValue().fromJson(EventFile.Deserializer()))
//
//                    EventFormFieldModel.Type.BOOLEAN ->
//                        Choice(field, findValue().fromJson<Boolean>())
//
//                    EventFormFieldModel.Type.PASSPORT ->
//                        Passport(field, findValue().fromJson<EventPassport>())
//
//                    else -> null
//                }
//            }
//        }
//    }
}