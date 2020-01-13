package com.example.ui.event.speakers

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
import com.example.extensions.findItemBy
import com.example.holders.SpeakerGroup
import com.example.holders.UserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.user.UserFragmentArgs
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class EventSpeakersFragment : BaseFragment(), EventSpeakersContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.about_event_speakers)

    @InjectPresenter
    lateinit var presenter: EventSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventSpeakersPresenter>

    @ProvidePresenter
    fun providePresenter(): EventSpeakersPresenter = presenterProvider.get().apply {
        eventId = EventSpeakersFragmentArgs.fromBundle(arguments!!).eventId
    }

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }


    override fun setData(data: List<Speaker>) {
        groupAdapter.update(data.map { speaker ->
            SpeakerGroup(
                    speaker,
                    { presenter.onSpeakerClick(it) },
                    { presenter.onSpeakerFavoriteChangeClick(it) }
            )
        })
        swipeToRefresh.isRefreshing = false
    }

    override fun updateSpeaker(speaker: Speaker) {
        val idLong = speaker.uid.toLong()
        groupAdapter.findItemBy { item: UserItem -> item.id == idLong }?.apply {
            notifyChanged(speaker.user.getUserSubscribeAction())
        }
    }

    override fun showSpeaker(speaker: Speaker) {
        findNavController().navigate(R.id.user_fragment, UserFragmentArgs.Builder(speaker.uid).build().toBundle())
    }

    override fun layout() = R.layout.layout_list
}
