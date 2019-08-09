package com.example.ui.speakers.favorite

import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
import com.example.ui.base.BaseFragment
import com.example.util.LayoutListWithPlaceholderUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteUsersFragment : BaseFragment(), FavoriteSpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteSpeakersPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteSpeakersPresenter = presenterProvider.get()

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@FavoriteUsersFragment.adapter
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply {
            doNotShowUntilDataLoad = true
            setMessage(getString(R.string.banned_empty_message))
            setImage(R.drawable.ic_neutral_face)
        }
    }

    override fun setData(data: PagedList<Speaker>) {
        placeholderUtil.isDataLoad = true
    }

    override fun showSpeaker(speaker: Speaker) {

    }

    override fun layout() = R.layout.layout_list_with_placeholder
}