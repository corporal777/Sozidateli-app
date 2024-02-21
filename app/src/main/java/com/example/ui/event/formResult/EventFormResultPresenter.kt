package com.example.ui.event.formResult

import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.data.models.AboutOrganizationData
import com.example.data.models.EventFile
import com.example.data.models.EventFormResultFieldsModel
import com.example.data.models.EventNew
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegisterFields
import com.example.data.models.EventRegisterResponseField
import com.example.data.models.UserFormResultModel
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.userprofile.edit.confirm.ConfirmEmailPhoneContract
import com.example.util.Utils
import com.google.gson.JsonElement
import fromJson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class EventFormResultPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseBottomSheetPresenter<EventFormResultContract.View>(appData),
    EventFormResultContract.Presenter {

    lateinit var formResult: UserFormResultModel

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Maybe.just(mapFields(formResult.formType?.fields))
            .map { Pair(it, mapFieldsResult(formResult.result?.fields, it)) }
            .map {
                val compressedList = it.first.filter { x -> it.second.any { f -> f.id == x.id } }
                createFieldsData(compressedList, it.second)
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
                EventRegisterField.Type.STRING,
                EventRegisterField.Type.TEXT_AREA,
                EventRegisterField.Type.DATE,
                EventRegisterField.Type.DATETIME,
                EventRegisterField.Type.DATETIMEPLANED,
                EventRegisterField.Type.NUMBER -> EventRegisterFieldData.String(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )

                EventRegisterField.Type.FILE -> EventRegisterFieldData.File(
                    field,
                    findRegistrationDataValue(
                        field,
                        responseField
                    ).fromJson(EventFile.Deserializer())
                )

                EventRegisterField.Type.PASSPORT -> EventRegisterFieldData.Passport(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<EventPassport>()
                )

                EventRegisterField.Type.SELECT_BOX -> EventRegisterFieldData.SelectBox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )

                EventRegisterField.Type.RADIO_BOX -> EventRegisterFieldData.RadioBox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )

                EventRegisterField.Type.CHECKBOXES,
                EventRegisterField.Type.CHECKBOX -> EventRegisterFieldData.Checkbox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<Set<String>>()
                )

                EventRegisterField.Type.BOOLEAN -> EventRegisterFieldData.Boolean(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<Boolean>()
                )

                else -> null
            }
        }
    }


    private fun findRegistrationDataValue(
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
}