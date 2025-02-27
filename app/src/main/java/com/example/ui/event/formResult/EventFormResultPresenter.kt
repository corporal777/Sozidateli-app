package com.example.ui.event.formResult

import com.example.data.AppData
import com.example.data.models.EventFile
import com.example.data.models.EventFormFieldModel
import com.example.data.models.EventFormResultFieldModel
import com.example.data.models.EventNew
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterProfilePrefilledFields.Companion.prefFromJson
import com.example.data.models.eventRegister.EventRegisterField
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultDateTime
import com.example.extensions.fromJson
import com.example.repository.EventRepository
import com.example.ui.base.bottomSheet.BaseBSPresenter
import com.google.gson.JsonElement
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EventFormResultPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
) : BaseBSPresenter<EventFormResultContract.View>(appData), EventFormResultContract.Presenter {

    lateinit var event: EventNew

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val formFields = event.binds?.getForm()?.fields
        val formResult = event.binds?.getFormResult()?.fields

        compositeDisposable += Maybe.defer {
            if (formFields.isNullOrEmpty() || formResult.isNullOrEmpty()) {
                eventRepository.getEventDetailForRegister(event.id.toString()).map { it.fieldsData }
            } else Maybe.just(createFieldsData(formFields, formResult))
        }
            .performOnBackgroundOutOnMain()
            .subscribe(
                {
                    viewState.setFormResult(it)
                }, {
                    it.printStackTrace()
                })
    }

    private fun createFieldsData(fields: List<EventFormFieldModel>?, results: List<EventFormResultFieldModel?>?): List<EventRegisterField<*>>? {

        return fields?.mapNotNull { field ->
            val findValue: () -> JsonElement? = { results?.find { field.id == it?.id }?.value }
            when (field.type) {
                EventFormFieldModel.Type.PREFILLED ->{
                    val value = results?.find { field.id == it?.id }?.fields
                    EventRegisterField.Prefilled(field, value.prefFromJson(field))
                }

                EventFormFieldModel.Type.SEPARATOR ->
                    EventRegisterField.Title(field,findValue().fromJson<String>() ?: field.name)

                EventFormFieldModel.Type.DATE -> {
                    val date = findValue().fromJson<String>()
                    EventRegisterField.Text(field, date?.formatToDefaultDate())
                }

                EventFormFieldModel.Type.DATETIME,
                EventFormFieldModel.Type.DATETIMEPLANED -> {
                    val date = findValue().fromJson<String>()
                    EventRegisterField.Text(field, date?.formatToDefaultDateTime())
                }

                EventFormFieldModel.Type.STRING,
                EventFormFieldModel.Type.TEXT_AREA,
                EventFormFieldModel.Type.EMAIL,
                EventFormFieldModel.Type.SITE,
                EventFormFieldModel.Type.PHONE,
                EventFormFieldModel.Type.NUMBER -> {
                    val data = findValue().fromJson<String>()
                    EventRegisterField.Text(field, data)
                }

                EventFormFieldModel.Type.FILE -> {
                    val file = findValue().fromJson(EventFile.Deserializer())
                    EventRegisterField.File(field, file)
                }

                EventFormFieldModel.Type.PASSPORT -> {
                    val passport = findValue().fromJson<EventPassport>()
                    EventRegisterField.Passport(field, passport)
                }

                EventFormFieldModel.Type.SELECT_BOX -> {
                    val selectBox = findValue().fromJson<String>()
                    EventRegisterField.SelectBox(field, selectBox)
                }

                EventFormFieldModel.Type.RADIO_BOX -> {
                    val radioBox = findValue().fromJson<String>()
                    EventRegisterField.RadioBox(field, radioBox)
                }

                EventFormFieldModel.Type.CHECKBOXES,
                EventFormFieldModel.Type.CHECKBOX ->
                    EventRegisterField.Checkbox(field, findValue().fromJson<Set<String>>())

                EventFormFieldModel.Type.BOOLEAN -> {
                    val value = findValue().fromJson<Boolean>() ?: false
                    EventRegisterField.Text(field, if (value) "Да" else "Нет")
                }

                else -> null
            }
        }
    }
}