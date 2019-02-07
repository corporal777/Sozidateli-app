package com.example.ui.profile

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class ProfilePresenter
@Inject constructor(
        private val appData: AppData,
        private val dummyRepository: DummyRepository
) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.apply {
            showLastNotification("Текст последней нотификации. Максимум 2 строки", 20)
        }
    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState?.apply {
            setUser(appData.getUser())
        }
    }

    override fun clickAboutStatus() = viewState.showAboutStatus()

    override fun clickFullProfile() = viewState.showFullProfile()

    override fun clickFavorite() = viewState.showFavorite()

    override fun clickMyEvents() = viewState.showMyEvents()

    override fun clickTabEvents() = viewState.showTabEvents()

    override fun clickCurrentEvent(event: Event) {
        appData.getUser().default_event?.let {
           // viewState.showCurrentEvent(it)
        }
    }

    override fun clickAboutApp() = viewState.showAboutApp()

    override fun clickChatSetting() = viewState.showChatSetting()

    override fun onNotificationClick() = viewState.showNotifications()
}
