package com.example.ui.views.galleryView

import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ProfileUserData
import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.views.calendarView.CalendarDay
import java.util.*

interface GalleryBottomContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setGalleryImages(images : List<Uri>)

        @StateStrategyType(SkipStrategy::class)
        fun hideGalleryFragment()
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
    }
}