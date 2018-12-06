package com.example.ui.eventSpeakers

import android.arch.paging.PagedList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.view.View
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.User
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_news_list.*
import kotlinx.android.synthetic.main.item_speaker.*
import javax.inject.Inject
import javax.inject.Provider

class SpeakersFragment : BaseFragment(), SpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: SpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<SpeakersPresenter>

    @ProvidePresenter
    fun providePresenter(): SpeakersPresenter = presenterProvider.get().apply {
        arguments?.let {
            if(it.containsKey(ARG_EVENT)) {
                val data = SpeakersFragmentArgs.fromBundle(it)
                event = data.event
            }
            onlyFavorite = it.getBoolean(ARG_ONLY_FAVORITE, false)
        }
    }

    companion object {
        private const val ARG_ONLY_FAVORITE = "only_fav"
        private const val ARG_EVENT = "event"


        fun newInstance(onlyFavorite: Boolean = false): SpeakersFragment {
            val fragment = SpeakersFragment()
            fragment.arguments = bundleOf(
                    ARG_ONLY_FAVORITE to onlyFavorite
            )
            return fragment
        }
    }

    private val adapter: SimplePagingRecyclerViewAdapter<User> by lazy {
        object : SimplePagingRecyclerViewAdapter<User>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_speaker

            override fun onBindItem(viewHolder: ViewHolder, item: User?, position: Int) {
                item!!
                viewHolder.apply {
                    itemView.setOnClickListener { presenter.onSpeakerClick(item) }
                    Picasso.get()
                            .load(item.image)
                            .transform(CropCircleTransformation())
                            .placeholder(R.drawable.ic_launcher)
                            .into(ivSpeakerAvatar)

                    tvSpeakerName.text = item.name
                    tvSpeakerInfo.text = item.info

                    val btnFavoriteBackground: Int
                    val btnFavoriteTextColor: Int
                    val btnFavoriteText: String
                    if (item.subscribed) {
                        btnFavoriteBackground = R.drawable.background_corners_border
                        btnFavoriteTextColor = ContextCompat.getColor(context!!, R.color.colorAccent)
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
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@SpeakersFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, VERTICAL))
        }
    }

    override fun setData(data: PagedList<User>) {
        adapter.submitList(data)
    }

    override fun showSpeaker(user: User) {

    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_speakers
}
