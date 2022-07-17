package com.example.ui.event.speakers.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MemberModel
import com.example.databinding.FragmentEventSpeakersBinding
import com.example.holders.PlaceholderItem
import com.example.holders.SpeakerGroup
import com.example.holders.redesign.ScreenHeaderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.util.IS_EXPANDED
import com.example.util.pagination.PaginationListGroupAdapter
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_speakers.*
import kotlinx.android.synthetic.main.fragment_recommendations.*
import javax.inject.Inject
import javax.inject.Provider

class EventSpeakersFragment : BaseFragmentNew<FragmentEventSpeakersBinding>(),
    EventSpeakersContract.View {

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
        eventId = EventSpeakersFragmentArgs.fromBundle(
            requireArguments()
        ).eventId
    }

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            //add(headerSection)
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
        mBinding.apply {
            speakersList.apply {
                adapter = groupAdapter
//                addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                        mDy += dy
//                        if (mDy >= 30) {
//                            appBarLayout.elevation = 10f
//                        } else {
//                            appBarLayout.elevation = 0f
//                        }
//                    }
//                })
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }

            appBarLayout.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
                })
        }

    }


    override fun setData(data: List<MemberModel?>) {
        speakersSection.update(data.map { speaker ->
            if (speaker == null) PlaceholderItem(PlaceholderItem.Type.SPEAKER_LIST)
            else SpeakerGroup(
                speaker,
            ) { presenter.onSpeakerClick(it) }
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showSpeaker(eventId: String, speaker: MemberModel) {
        findNavController().navigate(
            EventSpeakersFragmentDirections.actionEventSpeakerFragmentToUserSpeakerFragment(
                speaker.id.toString(),
                eventId
            )
        )
    }

//    override fun onStart() {
//        super.onStart()
//        if (mBinding.speakersList != null) {
//            mDy += mBinding.speakersList.scrollY
//        }
//    }

    private fun updateViews(offset: Float) {
        mBinding.apply {
            when {
                offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
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
