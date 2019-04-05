package com.example.ui.myEvents

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.models.Event
import com.example.data.models.Status
import com.example.events.OnUpdateMyEventsEvent
import com.example.extensions.build
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MyEventsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository
) : BasePresenter<MyEventsContract.View>(), MyEventsContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0

    private var pagination = PaginationDataSourceFactory { limit, offset -> eventRepository.getEventRegisterList(limit, offset) }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)
        pagination.build()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun attachView(view: MyEventsContract.View?) {
        super.attachView(view)
        viewState.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateList(event: OnUpdateMyEventsEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        pagination.invalidate()
    }

    override fun onEventClick(event: Event) {
        if (isCanSetDefault(event)) {
            eventRepository.setDefaultEvent(event.id)
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        eventData.event = event
                        viewState.selectEvent(event)
                    }, {
                        it.printStackTrace()
                    }).call(compositeDisposable)
        } else {
            eventData.event = event
            viewState.selectEvent(event)
        }
    }

    private fun isCanSetDefault(event: Event): Boolean {
        return event.status == Status.APPROVED.code || event.status == Status.CONFERENCE_IN_PROGRESS.code
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
