package com.example.ui.state.max

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.FileModel
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface MaxStateMainInfoContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPersonalData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun goToNext()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmail()

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setClickClose(type : Int)
    }
    interface Presenter : BaseContract.Presenter {
        fun onClickClose()
        fun updateFiles(data: MutableMap<String, Any?>)
        fun onChangeEmailClick()
        fun onChangeEmailConfirm(email: String, isFirst: Boolean)
    }
}