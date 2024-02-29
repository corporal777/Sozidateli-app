package com.example.ui.event.speakers.member

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.data.models.UserDetail
import com.example.databinding.FragmentUserSpeakerBinding
import com.example.extensions.dp
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.chat.ChatFragmentArgs
import com.example.ui.event.about.items.EventDetailActivitiesItem
import com.example.ui.event.about.items.EventDetailBlocksLabelItem
import com.example.ui.event.speakers.member.items.UserSpeakerMainInfoItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.UserSubscribeImageView
import com.example.ui.views.loading.CustomCircleLoadingButton
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserSpeakerFragment : BaseFragment<FragmentUserSpeakerBinding>(),
    UserSpeakerContract.View, ToolbarFragment {

    private val space by lazy {
        Space(requireContext()).apply {
            isVisible = false
            layoutParams = ViewGroup.LayoutParams(10.dp, 0)
        }
    }

    private val goToProfileButton by lazy {
        CustomCircleLoadingButton(requireContext()).apply {
            buttonText = requireContext().getString(R.string.go_to_profile)
            isVisible = false
            setOnClickListener { presenter.onGoToProfileClick() }
        }
    }

    private val addToFavoriteButton by lazy {
        UserSubscribeImageView(requireContext()).apply {
            isVisible = false
            setOnClickListener { presenter.onAddSpeakerToFavoriteClick() }
        }
    }

    @InjectPresenter
    lateinit var presenter: UserSpeakerPresenter


    @Inject
    lateinit var presenterProvider: Provider<UserSpeakerPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSpeakerPresenter = presenterProvider.get().apply {
        val args = UserSpeakerFragmentArgs.fromBundle(requireArguments())
        memberId = args.userId
        eventId = args.eventId
    }

    private val mainDataSection = Section()
    private val subEventsDataSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem(getString(R.string.speakers_activities_label)))
            setHideWhenEmpty(true)
        }
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(mainDataSection)
            add(subEventsDataSection)
        }
    }

    private val onEventClickListener = object : EventActivityItem.OnEventActivityClickListener {
        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) = presenter.onSubEventClick(subEvent)
        override fun onAddToScheduleClick(subEvent: EventActivityModel) = presenter.onAddToScheduleClick(subEvent)
        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) = presenter.onRemoveFromScheduleClick(subEvent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            listSpeakersContent.apply {
                this.adapter = groupAdapter
            }
        }
    }

    override fun setEmptyMainDataPlaceholder() {
        mainDataSection.updateItem(PlaceholderItem(PlaceholderItem.Type.SPEAKER_MAIN))
    }

    override fun setSpeakersMainInfo(speaker: MemberModel, isCurrentUser: Boolean) {
        mBinding.apply {
            addToFavoriteButton.isVisible = presenter.isUserRegistered() && !isCurrentUser
            goToProfileButton.isVisible = speaker.binds?.user?.state?.isRegistered ?: false
            space.isVisible = goToProfileButton.isVisible && addToFavoriteButton.isVisible
        }

        mainDataSection.updateItem(
            UserSpeakerMainInfoItem(
                speaker.id,
                presenter.isCurrentUser(),
                speaker.binds?.user?.nameLastName,
                speaker.organizationAndPosition,
                speaker.description,
                speaker.binds?.user?.loadUserImage(),
                speaker.getSpeakerStatus(),
                presenter.isUserRegistered()
            ) { presenter.onWriteMessageClick() }
        )
    }

    override fun setSpeakerActivities(canShow: Boolean, data: Map<String?, List<EventActivityModel>>?) {
        subEventsDataSection.update(
            data?.map {
                EventDetailActivitiesItem(
                    presenter.eventId,
                    canShow,
                    it.key ?: "",
                    it.value,
                    onEventClickListener
                )
            } ?: emptyList()
        )
    }

    override fun updateSpeaker(speaker: UserDetail) {
        addToFavoriteButton.setActionAlternative(speaker.binds?.userFavorite == null)
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        subEventsDataSection.findItemBy<EventActivityItem> { it.id == idLong }
            ?.notifyChanged(subEvent)
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        val args = ChatFragmentArgs.Builder(userName, chatId)
            .setUserAvatar(userAvatar).build().toBundle()
        findNavController().navigate(R.id.chat_fragment, args)
    }


    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun showUserProfile(userId: String) {
        val args = UserFragmentArgs.Builder(userId).build().toBundle()
        findNavController().navigate(R.id.user_fragment, args)
    }

    override fun showCurrentUserProfile() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun layout(): Int = R.layout.fragment_user_speaker
    override val title: CharSequence by lazy { "" }

    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            removeAllViews()
            addView(goToProfileButton, 0)
            addView(space)
            addView(addToFavoriteButton, 1)
        }
    }

    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}