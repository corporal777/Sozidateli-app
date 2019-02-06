package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.prefs.AppPrefs
import com.example.repository.DummyRepository
import com.example.repository.UserRepository
import com.example.repository.UserRepositoryImp
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
        private val appPrefs: AppPrefs,
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            when {
                appPrefs.userToken == null -> initWithAuth()
                appPrefs.selectedEvent != null -> loadEvent()
                else -> initWithEventList()
            }
        }

        userRepository.getUser()
                .performOnBackgroundOutOnMain()
                .subscribe({},{
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    private fun loadEvent() {
        //appData.event = dummyRepository.getEvent()
        viewState.initWithEvent()
    }

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)
}
