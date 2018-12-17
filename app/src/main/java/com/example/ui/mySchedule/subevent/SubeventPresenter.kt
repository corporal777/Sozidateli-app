package com.example.ui.mySchedule.subevent

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Subevent
import com.example.data.models.User
import com.example.repository.ChatRepository
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SubeventPresenter
@Inject constructor(private val dummyRepository: DummyRepository
) : BasePresenter<SubeventContract.View>(), SubeventContract.Presenter{


    lateinit var subevent:Subevent

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        dummyRepository.loadEventSpeakers("1",5,0)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setData(subevent,it.data)
                },{})
                .call(compositeDisposable)
    }


    override fun onSpeakerClick(user: User) {
        viewState.showSpeakerProfile(user)
    }

    override fun onShowMembersClick() {

    }
}
