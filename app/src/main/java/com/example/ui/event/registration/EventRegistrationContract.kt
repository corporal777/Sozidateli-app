package com.example.ui.event.registration

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract
import com.example.ui.event.registration.items.ProfileFieldsFormModel
import com.example.ui.event.registration.items.ProfileFieldsFormResult
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventRegistrationContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFormHeader(event: EventRegistration)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateProfileFields(profileForm : ProfileFieldsFormResult)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFormFields(
                event: EventRegistration,
                fieldsData: List<EventRegisterFieldData<*>>,
                withConfirm: Boolean
        )

        @StateStrategyType(SkipStrategy::class)
        fun showEventRegisterConfirmation()

        @StateStrategyType(SkipStrategy::class)
        fun showAgreementRegisterDialog(url: String)

        @StateStrategyType(SkipStrategy::class)
        fun showSaveFormResultDraftDialog()

        @StateStrategyType(SkipStrategy::class)
        fun showLoadSavedFormResultDraftDialog(result : EventRegisterData)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableActionButton(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun openFileSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFileField(fieldId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @StateStrategyType(SkipStrategy::class)
        fun showSuccessRegister(moderation: String?)

        @StateStrategyType(SkipStrategy::class)
        fun openUrl(url: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventLists()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEditProfile()

        @StateStrategyType(SkipStrategy::class)
        fun showEvent(eventId : String)

        @StateStrategyType(SkipStrategy::class)
        fun updateAppBarBackgroundColorValue(offset : Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPersonalDataFileClick(url: String)
        fun onAddFileClick(field: EventRegisterFieldData<EventFile?>)
        fun onFileSelected(path: Uri)
        fun onFileSelectionCancel()
        fun onRegisterClick()
        fun onRegisterCancelClick()
        fun onDataChange(field: EventRegisterFieldData<*>)
        fun onSelectedGroupChange(groupId: String?)

        fun onSuccessCancel()
        fun onSuccessGoToList()
        fun onSuccessGoToEvent()
        fun onBackClick()

        fun saveEventFormResultDraft()
        fun initFormResultData(event : EventRegistration, result : List<EventRegisterFieldData<*>>)
        fun changeAppBarBackground(value : Int)
    }
}
