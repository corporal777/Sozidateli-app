package com.example.ui.notifications

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class NotificationsPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        SimplePagination { limit, offset -> dummyRepository.loadUserNotifications(limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }
}
