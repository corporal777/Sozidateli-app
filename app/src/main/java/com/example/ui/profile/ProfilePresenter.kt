package com.example.ui.profile

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.repository.DummyRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ProfilePresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.hideLastNotification()
        userRepository.getLastNotification()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (it.isNotEmpty()) {
                        it[0].text?.let {
                            viewState.showLastNotification(it, 20)
                        }
                    }
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)

        userRepository.getUser()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState?.apply {
                        setUser(appData.getUser())
                    }
                },{
                    it.printStackTrace()
                })
                .call(compositeDisposable)
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
