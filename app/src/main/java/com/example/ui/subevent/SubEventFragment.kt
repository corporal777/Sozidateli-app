package com.example.ui.subevent

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.databinding.FragmentSubeventBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.holders.SubEventInfoItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.items.EventDetailBlocksLabelItem
import com.example.ui.event.speakers.member.UserSpeakerFragmentArgs
import com.example.ui.subevent.items.SubEventSpeakerItem
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class SubEventFragment : BaseFragment<FragmentSubeventBinding>(), SubEventContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: SubEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SubEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SubEventPresenter = presenterProvider.get().apply {
        val args = SubEventFragmentArgs.fromBundle(requireArguments())
        eventId = args.eventId
        subEventId = args.subEventId
    }

    private val infoSection = Section()
    private val speakersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.speakers)))
            setHideWhenEmpty(true)
        }
    }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(infoSection)
            add(speakersSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            contentList.apply {
                adapter = groupAdapter
            }
        }
    }

    override fun setSubEventPlaceholder() {
        infoSection.updateItem(PlaceholderItem(PlaceholderItem.Type.SUB_EVENT_MAIN))
    }

    override fun setData(isApproved: Boolean, subEvent: EventActivityModel) {
        infoSection.updateItem(
            SubEventInfoItem(isApproved, subEvent,
                { presenter.onAddToScheduleClick(it) },
                { presenter.onRemoveFromScheduleClick(it) },
                { }
            )
        )
    }

    override fun setSpeakers(speakers: List<MemberModel>) {
        speakersSection.update(speakers.map { speaker ->
            SubEventSpeakerItem(
                speaker.id,
                speaker.binds?.user?.nameLastName,
                speaker.organizationAndPosition,
                speaker.description,
                speaker.binds?.user?.image?.uri,
                speaker.status,
                speaker.binds?.user?.state?.isRegistered ?: false
            ) {
                presenter.onSpeakerClick(it)
            }
        })
    }

    override fun showSpeakerProfile(speaker: Int) {
        val args = UserSpeakerFragmentArgs.Builder(speaker.toString(), presenter.eventId)
            .build().toBundle()
        findNavController().navigate(R.id.user_speaker_fragment, args)
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent?.id?.toLong()
        infoSection.findItemBy<SubEventInfoItem> { it -> it.id == idLong }?.notifyChanged(subEvent)
    }

    override fun showEventErrorMessageDialog(withResult: Boolean, id: String, message: String) {
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {
            if (withResult) {
                setFragmentResult("eventKey", bundleOf("eventId" to id))
            }
            findNavController().navigateUp()
        }
    }

    override fun layout() = R.layout.fragment_subevent
    override val title: CharSequence by lazy { getString(R.string.event) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
