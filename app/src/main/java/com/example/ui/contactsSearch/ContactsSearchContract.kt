package com.example.ui.contactsSearch

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ContactSearch
import com.example.ui.base.BaseContract

interface ContactsSearchContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(contactSearch: PagedList<ContactSearch>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onScrollChange(position: Int, offset: Int)
        fun onQueryTextSubmit(text: String)
        fun onQueryTextChange(text: String)
        fun onSearchCollapsed()
    }
}
