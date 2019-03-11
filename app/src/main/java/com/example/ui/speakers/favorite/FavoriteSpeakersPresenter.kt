package com.example.ui.speakers.favorite

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Speaker
import com.example.repository.SpeakerRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.SimplePagination
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class FavoriteSpeakersPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val speakerRepository: SpeakerRepository
) : BasePresenter<FavoriteSpeakersContract.View>(), FavoriteSpeakersContract.Presenter {

    val pagination = SimplePagination { limit, offset -> userRepository.getFavoriteSpeakers(limit, offset) }
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination.build()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onSpeakerClick(speaker: Speaker) = viewState.showSpeaker(speaker)

    override fun onSpeakerFavoriteChangeClick(speaker: Speaker) {
        val id = speaker.id
        val request = if (speaker.isInFavorite != true) speakerRepository.addToFavorite(id)
        else speakerRepository.removeFromFavorite(id)
        request.performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    pagination.invalidate()
                }, {
                    pagination.invalidate()
                })
                .call(compositeDisposable)
    }
}
