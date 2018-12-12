package com.example.ui.search.searchType

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.DataArgsSearchType
import com.example.data.models.SearchTypeEvent
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface SearchTypeContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(data: ArrayList<SearchTypeEvent>)

    }

    interface Presenter : BaseContract.Presenter {
        fun save()
        fun selectItem(isSelect:Boolean,item:SearchTypeEvent)
        fun setData(data: ArrayList<SearchTypeEvent>,isPlaces:Boolean)
    }
}
