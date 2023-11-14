package com.example.ui.state.maxNew.interests

import com.example.data.models.InterestNew
import com.example.data.models.UserInterest
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import moxy.viewstate.strategy.alias.OneExecution

interface MaxStatusInterestsContract {
    interface View : BaseMaxStateContract.View {
        @OneExecution
        fun setInterestsData(interests: Map<InterestNew, List<UserInterest>>)
    }

    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveInterestsClick(data: List<InterestNew>)
    }
}