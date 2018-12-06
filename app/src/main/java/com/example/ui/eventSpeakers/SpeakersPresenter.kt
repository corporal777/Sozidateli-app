package com.example.ui.eventSpeakers

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.User
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.BackpressureStrategy
import javax.inject.Inject

@InjectViewState
class SpeakersPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<SpeakersContract.View>(), SpeakersContract.Presenter {

    lateinit var event: Event

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val factory = PaginationDataSourceFactory { limit, offset -> dummyRepository.loadEventSpeakers(event.id, limit, offset) }

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        RxPagedListBuilder(factory, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onSpeakerClick(user: User) = viewState.showSpeaker(user)

}
