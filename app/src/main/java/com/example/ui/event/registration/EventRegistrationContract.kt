package com.example.ui.event.registration

import com.example.data.models.EventFile
import com.example.data.models.EventRegisterData
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.ui.base.BaseContract
import com.example.ui.event.registration.items.PrefilledFieldClickType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventRegistrationContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setFormFields(event: EventRegistration, fieldsData: List<EventRegisterFieldData<*>>)

        @OneExecution
        fun updateProfileFields(profileForm : EventRegisterFieldData.Prefilled)

        @Skip
        fun showSaveFormResultDraftDialog()

        @Skip
        fun showSavedFormResultDraftDialog(res : EventRegisterData)

        @AddToEndSingle
        fun enableActionButton(enable: Boolean)

        @Skip
        fun openFileSelector(field: EventRegisterFieldData<EventFile?>)

        @OneExecution
        fun updateFileField(fieldId: String)

        @OneExecution
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @OneExecution
        fun showEventLists()

        @OneExecution
        fun showEditProfile(type: PrefilledFieldClickType)

        @OneExecution
        fun navigateUpClick()

        @OneExecution
        fun showErrors(invalidFields : MutableSet<EventRegisterFieldData<*>>)
    }

    interface Presenter : BaseContract.Presenter {

        fun onAddFileClick(field: EventRegisterFieldData<EventFile?>)
        fun onTakeFile(field: EventRegisterFieldData<EventFile?>)
        fun onTakeImage(field: EventRegisterFieldData<EventFile?>)

        fun onRegisterClick()
        fun onDataChange(field: EventRegisterFieldData<*>)

        fun onSuccessCancel()
        fun onSuccessGoToList()
        fun onNavigateUpClick()

        fun saveEventFormResultDraft()
        fun initFormResultData(event : EventRegistration, result : List<EventRegisterFieldData<*>>)
    }
}
