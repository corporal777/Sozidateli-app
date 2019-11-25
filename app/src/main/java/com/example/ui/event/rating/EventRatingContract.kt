package com.example.ui.event.rating

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventData
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterFieldData
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface EventRatingContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setFields(event: EventData, fieldsData: List<EventRegisterFieldData<*>>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableActionButton(enable: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun openFileSelector()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateFileField(fieldId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @StateStrategyType(SkipStrategy::class)
        fun showSuccessRate()

        @StateStrategyType(SkipStrategy::class)
        fun openUrl(url: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onPersonalDataFileClick(url: String)
        fun onAddFileClick(field: EventRegisterFieldData<EventFile?>)
        fun onFileSelected(path: Uri)
        fun onFileSelectionCancel()
        fun onSendClick()
        fun onDataChange(field: EventRegisterFieldData<*>)
        fun onRatingChange(rating: Int)
    }
}
