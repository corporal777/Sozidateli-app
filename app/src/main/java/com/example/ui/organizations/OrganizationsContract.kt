package com.example.ui.organizations

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Organization
import com.example.ui.base.BaseContract

interface OrganizationsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: PagedList<Organization>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onScrollChange(position: Int, offset: Int)
    }
}
