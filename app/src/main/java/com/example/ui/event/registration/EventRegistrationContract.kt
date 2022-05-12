package com.example.ui.event.registration

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventRegistrationContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setFields(
                event: EventRegistration,
                groupField: EventRegisterField?,
                selectedGroup: String?,
                groups: List<EventGroup>,
                fieldsData: List<EventRegisterFieldData<*>>,
                withConfirm: Boolean
        )

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEventRegisterConfirmation()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showAgreementRegisterDialog(url: String)

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

        @StateStrategyType(SkipStrategy::class)
        fun showEvent()
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
    }
}
