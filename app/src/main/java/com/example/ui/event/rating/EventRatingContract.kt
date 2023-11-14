package com.example.ui.event.rating

import android.net.Uri
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.data.models.FileModel
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EventRatingContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setFields(
                event: EventRegistration,
                fieldsData: List<EventRegisterFieldData<*>>,
                rating: Int,
                files: List<FileModel>?
        )

        @AddToEndSingle
        fun enableActionButton(enable: Boolean)

        @Skip
        fun openFileSelector()

        @OneExecution
        fun updateFileField(fieldId: String)

        @OneExecution
        fun showWrongFileExtensions(availableExtensions: List<String>)

        @Skip
        fun showSuccessRate()

        @Skip
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
