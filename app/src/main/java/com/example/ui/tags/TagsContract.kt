package com.example.ui.tags

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Tag
import com.example.ui.base.BaseContract

interface TagsContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(tags: List<Tag>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTitle(title: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onTagClick(tag: Tag)
    }
}
