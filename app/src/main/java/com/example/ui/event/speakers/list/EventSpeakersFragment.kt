package com.example.ui.event.speakers.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentEventSpeakersBinding
import com.example.data.models.MemberModel
import com.example.extensions.offsetChangedListener
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.subevent.items.SubEventSpeakerItem
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class EventSpeakersFragment : BaseVBFragment<FragmentEventSpeakersBinding>(),
    EventSpeakersContract.View {

    @InjectPresenter
    lateinit var presenter: EventSpeakersPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventSpeakersPresenter>

    @ProvidePresenter
    fun providePresenter(): EventSpeakersPresenter = presenterProvider.get().apply {
        eventId = EventSpeakersFragmentArgs.fromBundle(requireArguments()).eventId
    }

    private val speakersSection = Section()
    private val groupAdapter = GroupieAdapter().apply { add(speakersSection) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            speakersList.apply {
                adapter = groupAdapter
            }
            ivBack.setOnClickListener { findNavController().navigateUp() }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            appBarLayout.offsetChangedListener { appBarLayout, offset ->
                updateAppBarViews(abs(offset / appBarLayout.totalScrollRange).toFloat())
            }
        }
    }


    override fun setData(data: List<MemberModel?>) {
        speakersSection.update(data.map { speaker ->
            if (speaker == null) PlaceholderItem(PlaceholderItem.Type.SPEAKER_LIST)
            else SubEventSpeakerItem(
                requireContext(),
                speaker.id,
                speaker.binds?.user?.nameLastName,
                speaker.organizationAndPosition,
                speaker.description,
                speaker.binds?.user?.loadUserImage(),
                speaker.getSpeakerStatus()
            ) { presenter.onSpeakerClick(it) }
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showSpeaker(eventId: String, speakerId: Int) {
        val args = UserSpeakerFragmentArgs.Builder(speakerId.toString(), eventId).build().toBundle()
        findNavController().navigate(R.id.user_speaker_fragment, args)
    }

    override fun onExpandedState(withAnim: Boolean) {
        mBinding.tvLabelSmall.apply {
            if (withAnim) alpha = 1F
            if (withAnim) animate().setDuration(500).alpha(0.0f)
            visibility = View.GONE
        }
        mBinding.tvLabelLarge.apply {
            if (withAnim) alpha = 0F
            visibility = View.VISIBLE
            if (withAnim) animate().setDuration(500).alpha(1.0f)
        }
    }

    override fun onCollapsedState(withAnim: Boolean) {
        mBinding.tvLabelSmall.apply {
            if (withAnim) alpha = 0F
            visibility = View.VISIBLE
            if (withAnim) animate().setDuration(500).alpha(1.0f)
        }
        mBinding.tvLabelLarge.apply {
            if (withAnim) alpha = 1F
            if (withAnim) animate().setDuration(500).alpha(0.0f)
            visibility = View.GONE
        }
    }

    override fun binding() = FragmentEventSpeakersBinding::class.java
    override fun layout() = R.layout.fragment_event_speakers
}
