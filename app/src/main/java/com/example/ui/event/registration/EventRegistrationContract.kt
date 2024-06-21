package com.example.ui.event.registration

import android.net.Uri
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterData
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.ui.base.BaseContract
import com.example.data.models.ProfileFieldsFormResult
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventRegistrationContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setFormFields(
            event: EventRegistration,
            fieldsData: List<EventRegisterFieldData<*>>,
            withConfirm: Boolean
        )

        @OneExecution
        fun updateProfileFields(profileForm : ProfileFieldsFormResult)

        @Skip
        fun showSaveFormResultDraftDialog()

        @Skip
        fun showSavedFormResultDraftDialog(result : EventRegisterData)

        @AddToEndSingle
        fun enableActionButton(enable: Boolean)

        @Skip
        fun openFileSelector(field: EventRegisterFieldData<EventFile?>)

        @OneExecution
        fun updateFileField(fieldId: String)

        @OneExecution
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @OneExecution
        fun openUrl(url: String)

        @OneExecution
        fun showEventLists()

        @OneExecution
        fun showEditProfile()

        @Skip
        fun updateAppBarBackgroundColorValue(offset : Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPersonalDataFileClick(url: String)

        fun onAddFileClick(field: EventRegisterFieldData<EventFile?>)
        fun onTakeFile(field: EventRegisterFieldData<EventFile?>)
        fun onTakeImage(field: EventRegisterFieldData<EventFile?>)

        fun onRegisterClick()
        fun onDataChange(field: EventRegisterFieldData<*>)
        fun onSelectedGroupChange(groupId: String?)

        fun onSuccessCancel()
        fun onSuccessGoToList()
        fun onBackClick()

        fun saveEventFormResultDraft()
        fun initFormResultData(event : EventRegistration, result : List<EventRegisterFieldData<*>>)
        fun changeAppBarBackground(value : Int)
    }
}
