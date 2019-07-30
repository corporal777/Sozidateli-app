package com.example.ui.speakers.favorite

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.data.models.Speaker
import com.example.ui.speakers.base.BaseSpeakersFragment
import javax.inject.Inject
import javax.inject.Provider

class FavoriteSpeakersFragment : BaseSpeakersFragment(), FavoriteSpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteSpeakersPresenter>

    @ProvidePresenter
     fun providePresenter(): FavoriteSpeakersPresenter = presenterProvider.get()

    override fun onSpeakerFavoriteChangeClick(item: Speaker) {
        presenter.onSpeakerFavoriteChangeClick(item)
    }

    override fun onSpeakerClick(item: Speaker) {
        presenter.onSpeakerClick(item)
    }
}
