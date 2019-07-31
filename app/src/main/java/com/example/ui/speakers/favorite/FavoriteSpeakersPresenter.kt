package com.example.ui.speakers.favorite

import com.arellomobile.mvp.InjectViewState
import com.example.repository.UserRepository
import com.example.ui.speakers.base.BaseSpeakersPresenter
import javax.inject.Inject

@InjectViewState
class FavoriteSpeakersPresenter
@Inject constructor(
        private val userRepository: UserRepository
) : BaseSpeakersPresenter<FavoriteSpeakersContract.View>(userRepository), FavoriteSpeakersContract.Presenter {

}
