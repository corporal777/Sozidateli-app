package com.example.ui.event.formResult

import com.example.data.AppData
import com.example.data.models.EventFile
import com.example.data.models.EventFormResultFieldsModel
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegisterFields
import com.example.data.models.EventRegisterProfilePrefilledFields.Companion.prefilledFromJson
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.UserFormResultModel
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultDateTime
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.google.gson.JsonElement
import com.example.extensions.fromJson
import com.example.repository.EventRepository
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
    private val userRepository: UserRepository
) : BaseBottomSheetPresenter<EventFormResultContract.View>(appData),
    EventFormResultContract.Presenter {

    lateinit var formResult: UserFormResultModel

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += getProfileFieldsData()
            .map {
                val list = mapFields(it.first)
                createFieldsData(list, mapFieldsResult(it.second, list))
            }
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
                EventRegisterField.Type.PREFILLED -> {
                    EventRegisterFieldData.Prefilled(
                        field,
                        findFormResultValue(field, responseField).prefilledFromJson(field.values)
                    )
                }

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

    private fun getProfileFieldsData(): Maybe<Pair<List<EventRegisterFields>?, List<EventFormResultFieldsModel>>> {
        val fields = formResult.formType?.fields
        val results = arrayListOf<EventFormResultFieldsModel>().apply {
            addAll(formResult.result?.fields ?: emptyList())
        }
        val pref = fields?.find { x -> x.type == EventRegisterField.Type.PREFILLED }

        if (pref != null) {
            if (results.isEmpty() || results.none { it.id == pref.id })
                return eventRepository.getPrefilledEventFormResult(pref.id.toString())
                    .flatMapMaybe {
                        val jsonData = it.fields.prefilledToJson()
                        results.add(EventFormResultFieldsModel(pref.id, null, jsonData))
                        Maybe.just(Pair(fields, results))
                    }
            else return Maybe.just(Pair(fields, results))
        } else return Maybe.just(Pair(fields, results))
    }
}