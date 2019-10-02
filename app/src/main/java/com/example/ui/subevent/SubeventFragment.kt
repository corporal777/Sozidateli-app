package com.example.ui.subevent

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
import com.example.data.models.SubeventInfo
import com.example.holders.SpeakerItem
import com.example.holders.SpeakersListHeaderItem
import com.example.holders.SubeventInfoItem
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.ui.speaker.SpeakerFragmentArgs
import com.example.ui.subevent.users.SubeventUserListFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_subevent.*
import javax.inject.Inject
import javax.inject.Provider

class SubeventFragment : BaseNestedNavigationFragment(), SubeventContract.View {

    @InjectPresenter
    lateinit var presenter: SubeventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SubeventPresenter>

    @ProvidePresenter
    fun providePresenter(): SubeventPresenter = presenterProvider.get().apply {
        val args = SubeventFragmentArgs.fromBundle(arguments!!)
        event = args.eventId
        subevent = args.subeventId
    }

    private val infoSection = Section()
    private val speakersSection = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                add(infoSection)
                add(speakersSection)
            }
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }
    }

    override fun setData(subEvent: SubeventInfo) {
        infoSection.update(listOf(SubeventInfoItem(subEvent) {
            presenter.onOpenUserListClick()
        }))

        speakersSection.apply {
            if (subEvent.speakers.isEmpty()) {
                removeHeader()
                update(emptyList())
            } else {
                setHeader(SpeakersListHeaderItem())
                update(subEvent.speakers.map { speaker ->
                    SpeakerItem(speaker, { presenter.onSpeakerClick(it) }, { presenter.onSpeakerChangeSubscriptionClick(it) })
                })
            }
        }
    }

    override fun showSpeakerProfile(speaker: Speaker) {
        val args = SpeakerFragmentArgs.Builder(speaker).build().toBundle()
        findParentNavigation().navigate(R.id.speaker_fragment, args)
    }

    override fun openUserList(eventId: Int, subEventId: Int) {
        val args = SubeventUserListFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findParentNavigation().navigate(R.id.subevent_user_list_fragment, args)
    }

    override fun updateSpeaker(speaker: Speaker) {
        val idLong = speaker.id.toLong()
        for (i in 0 until speakersSection.itemCount) {
            val item = speakersSection.getItem(i)
            if (item.id == idLong && item is SpeakerItem) {
                item.updateSpeaker(speaker)
                break
            }
        }
    }

    override fun layout() = R.layout.fragment_subevent
}
