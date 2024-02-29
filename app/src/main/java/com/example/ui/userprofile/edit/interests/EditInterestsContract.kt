package com.example.ui.userprofile.edit.interests

import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.data.models.UserInterest
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EditInterestsContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setPlaceholder()

        @OneExecution
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSaveInterestsClick(data: List<InterestNew>)
    }
}