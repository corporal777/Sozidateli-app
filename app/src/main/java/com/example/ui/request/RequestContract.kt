package com.example.ui.request

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.RegisterEventFieldData
import com.example.data.models.RegistrationEvent
import com.example.ui.base.BaseContract

interface RequestContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun setFields(event: RegistrationEvent, fieldsData: List<RegisterEventFieldData<*>>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableActionButton(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun openFileSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFileField(fieldId: String, path: String)

        @StateStrategyType(SkipStrategy::class)
        fun showSuccessRegister()

        @StateStrategyType(SkipStrategy::class)
        fun openUrl(url: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onCloseClick()
        fun onDataChange(field: String, value: Any?, fieldForRemove: String? = null)
        fun onRegisterClick()
        fun onGoTeEventListClick()

        fun onPersonalDataFileClick(url: String)
        fun onAddFileClick(fieldId: String)
        fun onFileSelected(path: String)
        fun onFileSelectionCancel()
    }
}
