package com.example.ui.event.registration

import com.example.data.models.EventFile
import com.example.data.models.EventRegisterData
import com.example.data.models.eventRegister.EventRegisterField
import com.example.data.models.EventRegistration
import com.example.ui.base.BaseContract
import com.example.ui.event.registration.items.PrefilledFieldClickType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventRegistrationContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setFormFields(event: EventRegistration, fieldsData: List<EventRegisterField<*>>)

        @Skip
        fun updateProfileFields(profileForm : EventRegisterField.Prefilled)

        @Skip
        fun updateFileField(fieldId: String)

        @Skip
        fun showSaveFormResultDraftDialog()

        @Skip
        fun showSavedFormResultDraftDialog(res : EventRegisterData)

        @Skip
        fun openFileSelector(field: EventRegisterField<EventFile?>)

        @Skip
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @OneExecution
        fun showEventLists()

        @OneExecution
        fun showEditProfile(type: PrefilledFieldClickType)

        @OneExecution
        fun navigateUpClick()

        @Skip
        fun enableActionButton(enable: Boolean)

        @Skip
        fun showErrors(invalidFields : MutableSet<EventRegisterField<*>>)
    }

    interface Presenter : BaseContract.Presenter {

        fun onAddFileClick(field: EventRegisterField<EventFile?>)
        fun onTakeFile(field: EventRegisterField<EventFile?>)
        fun onTakeImage(field: EventRegisterField<EventFile?>)

        fun onRegisterClick()
        fun onDataChange(field: EventRegisterField<*>)

        fun onSuccessCancel()
        fun onSuccessGoToList()
        fun onNavigateUpClick()

        fun saveEventFormResultDraft()
        fun initFormResultData(event : EventRegistration, result : List<EventRegisterField<*>>)
    }
}
