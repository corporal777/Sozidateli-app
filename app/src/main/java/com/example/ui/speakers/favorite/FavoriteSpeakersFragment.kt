package com.example.ui.speakers.favorite

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Speaker
import com.example.ui.base.BaseFragment
import com.example.util.ARG_SPEAKER
import com.example.util.ARG_USER
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_speaker.*
import setAvatar
import javax.inject.Inject
import javax.inject.Provider

class FavoriteSpeakersFragment : BaseFragment(), FavoriteSpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteSpeakersPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteSpeakersPresenter = presenterProvider.get()

    private val adapter: SimplePagingRecyclerViewAdapter<Speaker> by lazy {
        object : SimplePagingRecyclerViewAdapter<Speaker>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_speaker

            override fun onBindItem(viewHolder: ViewHolder, item: Speaker?, position: Int) {
                item!!
                viewHolder.apply {
                    ivSpeakerAvatar.setAvatar(item.photo)

                    tvSpeakerName.text = item.name
                    tvSpeakerInfo.text = item.description

                    val btnFavoriteBackground: Int
                    val btnFavoriteTextColor: Int
                    val btnFavoriteText: String

                    if (item.isInFavorite) {
                        btnFavoriteBackground = R.drawable.background_corners_border
                        btnFavoriteTextColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                        btnFavoriteText = getString(R.string.remove_from_favorites)
                    } else {
                        btnFavoriteBackground = R.drawable.background_corners
                        btnFavoriteTextColor = Color.WHITE
                        btnFavoriteText = getString(R.string.add_to_favorites)
                    }

                    btnSubscribe.apply {
                        setBackgroundResource(btnFavoriteBackground)
                        setTextColor(btnFavoriteTextColor)
                        text = btnFavoriteText
                        setOnClickListener { presenter.onSpeakerFavoriteChangeClick(item) }
                    }

                    itemView.setOnClickListener { presenter.onSpeakerClick(item) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@FavoriteSpeakersFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, VERTICAL))
        }
    }

    override fun setData(data: PagedList<Speaker>) {
        adapter.submitList(data)
    }

    override fun showSpeaker(speaker: Speaker) {
        findNavController().navigate(R.id.speaker_fragment, bundleOf(
                ARG_SPEAKER to speaker
        ))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_speakers
}
