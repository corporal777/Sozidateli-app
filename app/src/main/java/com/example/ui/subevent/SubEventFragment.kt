package com.example.ui.subevent

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.databinding.FragmentSubeventBinding
import com.example.extensions.findItemBy
import com.example.holders.SpeakerGroup
import com.example.holders.SubeventInfoItem
import com.example.holders.redesign.ScreenHeaderItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.items.EventDetailBlocksLabelItem
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class SubEventFragment : BaseFragmentNew<FragmentSubeventBinding>(), SubEventContract.View {


    @InjectPresenter
    lateinit var presenter: SubEventPresenter

    private var mDy: Int = 0

    @Inject
    lateinit var presenterProvider: Provider<SubEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SubEventPresenter = presenterProvider.get().apply {
        val args = SubEventFragmentArgs.fromBundle(requireArguments())
        eventId = args.eventId
        subEventId = args.subEventId
    }

    private val headerSection = Section()
    private val infoSection = Section()
    private val speakersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.speakers)).apply {

            })
            setHideWhenEmpty(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mDy = 0
        val mO = 1.0f

        mBinding.apply {
            contentList.apply {
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    //add(headerSection)
                    add(infoSection)
                    add(speakersSection)
                }
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        mDy += dy
                        val mAlpha = abs(mDy / 100f)
                        val mAlphaBig = -abs(mDy / 100f)
                        val mElevation = abs(mDy / 10f)
                        appBarLayout.apply {
                            elevation = if (mElevation <= 10f) {
                                mElevation
                            } else {
                                10f
                            }
                        }
                    }
                })
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun setData(isApproved: Boolean, subEvent: EventActivityModel) {
        headerSection.update(listOf(ScreenHeaderItem(getString(R.string.event))))
        infoSection.update(listOf(SubeventInfoItem(isApproved, subEvent, {
            presenter.onAddToScheduleClick(it)
        }, {
            presenter.onRemoveFromScheduleClick(it)
        }, {
            showToast(it.toString())
        })))
    }

    override fun setSpeakers(speakers: List<MemberModel>) {
        speakersSection.update(speakers.map { speaker ->
            SpeakerGroup(
                speaker
            ) { presenter.onSpeakerClick(it) }
        })
    }

    override fun showSpeakerProfile(speaker: MemberModel) {
        findNavController().navigate(
            SubEventFragmentDirections.actionSubEventFragmentToUserSpeakerFragment(
                speaker.id.toString(),
                presenter.eventId
            )
        )
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent?.id?.toLong()
        infoSection.findItemBy<SubeventInfoItem> { it -> it.id == idLong }?.notifyChanged(subEvent)
    }

//    override fun onStart() {
//        super.onStart()
//        if (mBinding.contentList!= null) {
//            mDy += mBinding.contentList.scrollY
//        }
//    }

    override fun showEventErrorMessageDialog(withResult: Boolean, id: String, message: String) {
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            if (withResult) {
                setFragmentResult("eventKey", bundleOf("eventId" to id))
            }
            findNavController().navigateUp()
        }

    }

    override fun layout() = R.layout.fragment_subevent
}
