package com.example.ui.event.formResult

import com.example.data.AppData
import com.example.data.models.EventFile
import com.example.data.models.EventFormResultFieldsModel
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegisterFields
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.Optional
import com.example.data.models.UserFormResultModel
import com.example.data.models.asOptional
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultDateTime
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.google.gson.JsonElement
import com.example.extensions.fromJson
import com.example.repository.EventRepository
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EventFormResultPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : BaseBottomSheetPresenter<EventFormResultContract.View>(appData),
    EventFormResultContract.Presenter {

    lateinit var formResult: UserFormResultModel

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Maybe.just(mapFields(formResult.formType?.fields))
            .map { Pair(it, mapFieldsResult(formResult.result?.fields, it)) }
            .map {
                //val compressedList = it.first.filter { x -> it.second.any { f -> f.id == x.id } }
                //createFieldsData(compressedList, it.second)
                createFieldsData(it.first, it.second)
            }
            .flatMap { getProfileFieldsData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setFormResult(it)
            }
    }

    private fun createFieldsData(
        fields: List<EventRegisterField>?,
        responseField: List<EventRegisterResponseField?>?
    ): List<EventRegisterFieldData<*>>? {
        return fields?.mapNotNull { field ->
            when (field.type) {
                EventRegisterField.Type.DATE -> {
                    val date = findFormResultValue(field, responseField).fromJson<String>()
                    EventRegisterFieldData.String(field, date?.formatToDefaultDate())
                }

                EventRegisterField.Type.DATETIME,
                EventRegisterField.Type.DATETIMEPLANED -> {
                    val date = findFormResultValue(field, responseField).fromJson<String>()
                    EventRegisterFieldData.String(field, date?.formatToDefaultDateTime())
                }

                EventRegisterField.Type.STRING,
                EventRegisterField.Type.TEXT_AREA,
                EventRegisterField.Type.NUMBER -> {
                    val data = findFormResultValue(field, responseField).fromJson<String>()
                    EventRegisterFieldData.String(field, data)
                }

                EventRegisterField.Type.FILE -> {
                    val file =
                        findFormResultValue(field, responseField).fromJson(EventFile.Deserializer())
                    EventRegisterFieldData.File(field, file)
                }

                EventRegisterField.Type.PASSPORT -> {
                    val passport =
                        findFormResultValue(field, responseField).fromJson<EventPassport>()
                    EventRegisterFieldData.Passport(field, passport)
                }

                EventRegisterField.Type.SELECT_BOX -> {
                    val selectBox = findFormResultValue(field, responseField).fromJson<String>()
                    EventRegisterFieldData.SelectBox(field, selectBox)
                }

                EventRegisterField.Type.RADIO_BOX -> {
                    val radioBox = findFormResultValue(field, responseField).fromJson<String>()
                    EventRegisterFieldData.RadioBox(field, radioBox)
                }

                EventRegisterField.Type.CHECKBOXES,
                EventRegisterField.Type.CHECKBOX -> EventRegisterFieldData.Checkbox(
                    field,
                    findFormResultValue(field, responseField).fromJson<Set<String>>()
                )

                EventRegisterField.Type.BOOLEAN -> {
                    val value = findFormResultValue(field, responseField).fromJson<Boolean>()
                    val answer = if (value == true) "Да" else "Нет"
                    EventRegisterFieldData.String(field, answer)
                }

                else -> null
            }
        }
    }


    private fun findFormResultValue(
        field: EventRegisterField,
        fields: List<EventRegisterResponseField?>?
    ): JsonElement? {
        return fields?.find { field.id == it?.id }?.value
    }

    private fun mapFields(it: List<EventRegisterFields>?): List<EventRegisterField> {
        val result = mutableListOf<EventRegisterField>()
        it?.forEach { field -> result.add(field.createData()) }
        return result
    }

    private fun mapFieldsResult(
        it: List<EventFormResultFieldsModel>?,
        fields: List<EventRegisterField>?
    ): List<EventRegisterResponseField> {
        val result = mutableListOf<EventRegisterResponseField>()
        it?.forEach { field ->
            val type = fields?.firstOrNull { it.id == field.id.toString() }
            result.add(field.createData(type?.type))
        }
        return result
    }

    private fun getProfileFieldsData(list: List<EventRegisterFieldData<*>>): Maybe<List<EventRegisterFieldData<*>>> {
        val field =
            formResult.formType?.fields?.find { x -> x.type == EventRegisterField.Type.PREFILLED }
        if (field != null) {
            return eventRepository.loadEventFormResult(field.id.toString())
                .flatMapMaybe {
                    val data = EventRegisterFieldData.Prefilled(
                        field.createData(),
                        it.toFormResult(field.parameters?.options)
                    )
                    Maybe.just(list.plus(data).sortedBy { x -> x.field.id })
                }

        } else return Maybe.just(list)
    }
}