package com.example.ui.newsList

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.News
import com.example.ui.base.BaseContract

interface NewsListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(news: PagedList<News>)

        @StateStrategyType(SkipStrategy::class)
        fun showNews(news: News)
    }

    interface Presenter : BaseContract.Presenter {
        fun onNewsClick(news: News)
    }
}
