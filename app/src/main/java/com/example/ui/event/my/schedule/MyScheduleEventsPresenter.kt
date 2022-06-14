package com.example.ui.event.my.schedule

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.getMonthName
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@InjectViewState
class MyScheduleEventsPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository
) : BasePresenter<MyScheduleEventsContract.View>(appData), MyScheduleEventsContract.Presenter {


    private var mDate = ""


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEventData()
    }

    private fun getEventData() {
        compositeDisposable += userEventData.loadEventData("1248")
            .withCheckInternetConnectivity()
            .withProgressBarLoadingDialog(viewState)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                Log.e("ActivitiesFragment", "Events: " + it.activity.activities.size)
                mDate = it.eventInfo.event.holdingDate?.from ?: ""
                initCalendarDays()

            }
    }

    private fun getDays(): List<String> {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var mDates = arrayListOf<String>()
        val mEventDate = defaultServerDateFormatter.parse(mDate).time
        var mCal = mEventDate.calendar()
        val mMaxDays = mCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until mMaxDays) {
            mCal[Calendar.DAY_OF_MONTH] = i + 1
            mDates.add(df.format(mCal.time))
        }

        return mDates
    }


    private fun initCalendarDays() {
        val mEventDate = defaultServerDateFormatter.parse(mDate).time
        val mMonth =
            getMonthName(mEventDate.calendar().get(Calendar.MONTH))
        val mDays = userEventData.createCalendarDays(getDays().map {
            defaultServerDateFormatter.parse(it).time
        })
        viewState.apply {
            setHeaderAndCalendar(mMonth, mDays)
            setSearchBlock()
        }
    }


    override fun onSearchTextChange(text: String) {
    }

    override fun onSearchTextSubmit(text: String) {
    }


}