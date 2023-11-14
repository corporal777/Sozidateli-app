package com.example.ui.event.registration

import android.net.Uri
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterData
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.ui.base.BaseContract
import com.example.ui.event.registration.items.ProfileFieldsFormResult
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventRegistrationContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setContentPlaceholder()

        @AddToEndSingle
        fun setFormHeader(event: EventRegistration)

        @AddToEndSingle
        fun setFormFields(
            event: EventRegistration,
            fieldsData: List<EventRegisterFieldData<*>>,
            withConfirm: Boolean
        )

        @OneExecution
        fun updateProfileFields(profileForm : ProfileFieldsFormResult)

        @Skip
        fun showEventRegisterConfirmation()

        @Skip
        fun showSaveFormResultDraftDialog()

        @Skip
        fun showLoadSavedFormResultDraftDialog(result : EventRegisterData)

        @AddToEndSingle
        fun enableActionButton(enable: Boolean)

        @Skip
        fun openFileSelector()

        @OneExecution
        fun updateFileField(fieldId: String)

        @OneExecution
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @Skip
        fun showSuccessRegister(moderation: String?)

        @OneExecution
        fun openUrl(url: String)

        @Skip
        fun showEventLists()

        @OneExecution
        fun showEditProfile()

        @OneExecution
        fun showEvent(eventId : String)

        @Skip
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
