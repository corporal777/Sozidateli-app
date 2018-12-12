package com.example.ui.mySchedule

import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MySchedulePresenter
@Inject constructor(
) : BasePresenter<MyScheduleContract.View>(), MyScheduleContract.Presenter {

}
