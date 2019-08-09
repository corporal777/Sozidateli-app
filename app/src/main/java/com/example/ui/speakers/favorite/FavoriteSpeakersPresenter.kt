package com.example.ui.speakers.favorite

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Speaker
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class FavoriteSpeakersPresenter
@Inject constructor(
        private val userRepository: UserRepository
) : BasePresenter<FavoriteSpeakersContract.View>(), FavoriteSpeakersContract.Presenter {

    override fun onSpeakerClick(speaker: Speaker) {

    }

    override fun onSpeakerFavoriteChangeClick(speaker: Speaker) {

    }
}
