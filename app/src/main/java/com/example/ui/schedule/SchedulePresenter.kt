package com.example.ui.schedule

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class SchedulePresenter
@Inject constructor(
) : BasePresenter<ScheduleContract.View>(), ScheduleContract.Presenter {

}
