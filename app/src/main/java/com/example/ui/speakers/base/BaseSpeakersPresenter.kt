package com.example.ui.speakers.base

import call
import com.example.data.models.Speaker
import com.example.extensions.build
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

open class BaseSpeakersPresenter<V : BaseSpeakersContract.View>
@Inject constructor(
        private val userRepository: UserRepository
) : BasePresenter<V>(), BaseSpeakersContract.Presenter {

    open val pagination = PaginationDataSourceFactory { limit, offset -> userRepository.getFavoriteSpeakers(limit, offset) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination.build()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onSpeakerClick(speaker: Speaker) = viewState.showSpeaker(speaker)

    override fun onSpeakerFavoriteChangeClick(speaker: Speaker) {
        val id = speaker.id.toString()
        val request = if (!speaker.isInFavorite) userRepository.addToFavorite(id)
        else userRepository.removeFromFavorite(id)
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
