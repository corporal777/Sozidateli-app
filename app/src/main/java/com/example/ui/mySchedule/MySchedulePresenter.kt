package com.example.ui.mySchedule

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Subevent
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class MySchedulePresenter
@Inject constructor(private val dummyRepository: DummyRepository
) : BasePresenter<MyScheduleContract.View>(), MyScheduleContract.Presenter {


    var isMySchedule = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        selectDate(1543968000000)
        if (isMySchedule) {
            viewState.setTagsAndDays(null, 1543968000000, 1545264000000)
        } else {
            dummyRepository.loadTags()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        viewState.setTagsAndDays(it, 1543968000000, 1545264000000)
                    }, {})
                    .call(compositeDisposable)
        }
    }

    override fun selectDate(date: Long) {
        dummyRepository.loadSubevent(isMySchedule)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.updateSubevents(date, it, isMySchedule)
                }, {

                }).call(compositeDisposable)
    }

    override fun changeSelectedTags(tags: ArrayList<String>) {

    }

    override fun addToSchedule(subevent: Subevent) {

    }

    override fun removeFromeSchedule(subevent: Subevent) {
    }

    override fun onSubeventClick(subevent: Subevent) {
        viewState.openSubevent(subevent,isMySchedule)
    }
}
