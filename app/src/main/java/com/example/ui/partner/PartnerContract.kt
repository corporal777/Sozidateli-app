package com.example.ui.partner

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Partner
import com.example.data.models.PartnerModel
import com.example.ui.base.BaseContract

interface PartnerContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(partner: PartnerModel, logo: Bitmap?, background: Bitmap?)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation
}
