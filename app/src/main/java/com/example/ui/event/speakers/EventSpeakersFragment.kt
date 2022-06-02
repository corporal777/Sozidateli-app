package com.example.ui.event.speakers

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MemberModel
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.SpeakerGroup
import com.example.holders.UserItem
import com.example.holders.redesign.ScreenHeaderItem
import com.example.ui.base.BaseFragment
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_speakers.*
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class EventSpeakersFragment : BaseFragment(), EventSpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: EventSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventSpeakersPresenter>

    private var mDy = 0

    private val headerSection by lazy {
        Section().apply {
            update(listOf(ScreenHeaderItem(getString(R.string.speakers))))
        }
    }
    private val speakersSection = Section()

    @ProvidePresenter
    fun providePresenter(): EventSpeakersPresenter = presenterProvider.get().apply {
        eventId = EventSpeakersFragmentArgs.fromBundle(requireArguments()).eventId
    }

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(headerSection)
            add(speakersSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        speakersList.apply {
            adapter = groupAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mDy += dy
                    if (mDy >= 30) {
                        speakerAppBar.elevation = 10f
                    } else {
                        speakerAppBar.elevation = 0f
                    }
                }
            })
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }


    override fun setData(data: List<MemberModel?>) {
        //groupAdapter.update(data.map { speaker ->
        speakersSection.update(data.map { speaker ->
            if (speaker == null) PlaceholderItem(PlaceholderItem.Type.SPEAKER_LIST)
            else SpeakerGroup(
                speaker,
                { presenter.onSpeakerClick(it) },
                { presenter.onSpeakerFavoriteChangeClick(it) }
            )


        })
        swipeToRefresh.isRefreshing = false
    }

    override fun updateSpeaker(speaker: MemberModel) {
        val idLong = speaker.user?.toLong()/*speaker.uid.toLong()*/
        groupAdapter.findItemBy { item: UserItem -> item.id == idLong }?.apply {
            //Speaker
            notifyChanged(speaker.binds?.user?.getUserSubscribeAction())
        }
    }

    override fun showSpeaker(eventId: String, speaker: MemberModel) {
        findNavController().navigate(
            EventSpeakersFragmentDirections.actionEventSpeakerFragmentToUserSpeakerFragment(
                speaker.id.toString(),
                eventId
            )
        )
//        findNavController().navigate(
//            R.id.user_speaker_fragment,
//            UserSpeakerFragmentArgs.Builder(speaker.user.toString(), eventId).build().toBundle()
//        )
        //findNavController().navigate(R.id.user_fragment, UserFragmentArgs.Builder(speaker.user.toString()).build().toBundle())
    }

    override fun onStart() {
        super.onStart()
        if (speakersList != null) {
            mDy += speakersList.scrollY
        }
    }

    override fun layout() = R.layout.fragment_event_speakers
}
