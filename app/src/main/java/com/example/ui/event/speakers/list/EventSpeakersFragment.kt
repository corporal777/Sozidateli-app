package com.example.ui.event.speakers.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MemberModel
import com.example.databinding.FragmentEventSpeakersBinding
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.subevent.items.SubEventSpeakerItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import offsetChangedListener
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class EventSpeakersFragment : BaseFragment<FragmentEventSpeakersBinding>(),
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
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(speakersSection)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            speakersList.apply {
                adapter = groupAdapter
            }
            ivBack.setOnClickListener { findNavController().navigateUp() }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            appBarLayout.offsetChangedListener { appBarLayout, offset ->
                updateViews(abs(offset / appBarLayout.totalScrollRange.toFloat()))
            }
        }
    }


    override fun setData(data: List<MemberModel?>) {
        speakersSection.update(data.map { speaker ->
            if (speaker == null) PlaceholderItem(PlaceholderItem.Type.SPEAKER_LIST)
            else SubEventSpeakerItem(
                speaker.id,
                speaker.binds?.user?.nameLastName,
                speaker.organizationAndPosition,
                speaker.description,
                speaker.binds?.user?.loadUserImage(),
                speaker.status,
                speaker.binds?.user?.state?.isRegistered ?: false
            ) { presenter.onSpeakerClick(it) }
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showSpeaker(eventId: String, speakerId: Int) {
        val args = UserSpeakerFragmentArgs.Builder(speakerId.toString(), eventId).build().toBundle()
        findNavController().navigate(R.id.user_speaker_fragment, args)
    }

    private fun updateViews(offset: Float) {
        mBinding.apply {
            when {
                offset < SWITCH_BOUND -> Pair(
                    TO_EXPANDED,
                    cashCollapseState?.second ?: WAIT_FOR_SWITCH
                )
                else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            }.apply {
                when {
                    cashCollapseState != null && cashCollapseState != this -> {
                        when (first) {
                            TO_EXPANDED -> {
                                tvLabelSmall.apply {
                                    alpha = 1F
                                    animate().setDuration(500).alpha(0.0f)
                                    visibility = View.GONE
                                }

                                tvLabelLarge.apply {
                                    visibility = View.VISIBLE
                                    alpha = 0F
                                    animate().setDuration(500).alpha(1.0f)
                                }
                            }
                            TO_COLLAPSED -> {
                                tvLabelSmall.apply {
                                    alpha = 0F
                                    animate().setDuration(500).alpha(1.0f)
                                    tvLabelSmall.visibility = View.VISIBLE
                                }
                                tvLabelLarge.apply {
                                    alpha = 1F
                                    animate().setDuration(500).alpha(0.0f)
                                    visibility = View.GONE
                                }
                            }
                        }
                        cashCollapseState = Pair(first, SWITCHED)
                    }
                    else -> {
                        cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                    }
                }
            }
        }
    }

    private var cashCollapseState: Pair<Int, Int>? = null

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }


    override fun layout() = R.layout.fragment_event_speakers
}
