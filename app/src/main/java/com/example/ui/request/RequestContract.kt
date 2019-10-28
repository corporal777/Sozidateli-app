package com.example.ui.request

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventFile
import com.example.data.models.EventGroup
import com.example.data.models.RegisterEventFieldData
import com.example.data.models.RegistrationEvent
import com.example.ui.base.BaseContract

interface RequestContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun setFields(event: RegistrationEvent, selectedGroup: String?, groups: List<EventGroup>, fieldsData: List<RegisterEventFieldData<*>>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableActionButton(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun openFileSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFileField(fieldId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @StateStrategyType(SkipStrategy::class)
        fun showSuccessRegister()

        @StateStrategyType(SkipStrategy::class)
        fun openUrl(url: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPersonalDataFileClick(url: String)
        fun onAddFileClick(field: RegisterEventFieldData<EventFile?>)
        fun onFileSelected(path: Uri)
        fun onFileSelectionCancel()
        fun onRegisterClick()
        fun onDataChange(field: RegisterEventFieldData<*>)
        fun onSelectedGroupChange(groupId: String?)
    }
}
