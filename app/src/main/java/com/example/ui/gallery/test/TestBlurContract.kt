package com.example.ui.gallery.test

import android.net.Uri
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract

interface TestBlurContract {

    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setImages(images : List<Uri>)
    }

    interface Presenter : BaseContract.Presenter {

    }
}