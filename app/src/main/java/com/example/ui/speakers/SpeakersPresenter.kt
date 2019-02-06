package com.example.ui.speakers

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import javax.inject.Inject

@InjectViewState
class SpeakersPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<SpeakersContract.View>(), SpeakersContract.Presenter {

    var event: Event?=null
    var onlyFavorite = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
       /* SimplePagination { limit, offset -> if(onlyFavorite) dummyRepository.loadFavoriteSpeakers(limit, offset) else dummyRepository.loadEventSpeakers(event!!.id, limit, offset) }
                .create()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)*/
    }

    override fun onSpeakerClick(user: User) = viewState.showSpeaker(user)

}
