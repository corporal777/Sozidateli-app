package com.example.ui.notifications

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class NotificationsPresenter
@Inject constructor(
        private val userRepository: UserRepository
) : BasePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        SimplePagination { limit, offset -> userRepository.getNotifications(limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }
}
