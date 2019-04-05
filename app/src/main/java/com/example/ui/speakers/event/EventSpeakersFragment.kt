package com.example.ui.speakers.event

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
import com.example.ui.speakers.base.BaseSpeakersFragment
import com.example.ui.speakers.favorite.FavoriteSpeakersPresenter
import com.example.util.ARG_SPEAKER
import com.example.util.ARG_USER
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_speaker.*
import setCircleImageWithPlaceholder
import javax.inject.Inject
import javax.inject.Provider

class EventSpeakersFragment : BaseSpeakersFragment(), EventSpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: EventSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventSpeakersPresenter>

    @ProvidePresenter
    fun providePresenter(): EventSpeakersPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.speakers)
    }

    override fun onSpeakerFavoriteChangeClick(item: Speaker) {
        presenter.onSpeakerFavoriteChangeClick(item)
    }

    override fun onSpeakerClick(item: Speaker) {
        presenter.onSpeakerClick(item)
    }
}
