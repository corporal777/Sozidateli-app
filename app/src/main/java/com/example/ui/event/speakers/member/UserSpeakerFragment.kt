package com.example.ui.event.speakers.member

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.data.models.UserDetail
import com.example.databinding.FragmentUserSpeakerBinding
import com.example.extensions.dp
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.items.EventDetailActivitiesBlock
import com.example.ui.event.about.redesign.items.EventDetailBlocksLabelItem
import com.example.ui.event.activities.items.NoSubEventItem
import com.example.ui.event.speakers.member.items.UserSpeakerMainInfoItem
import com.example.ui.subevent.SubEventFragmentArgs
import com.example.ui.views.dialogs_new.EventAddedToFavoriteDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class UserSpeakerFragment : BaseFragmentNew<FragmentUserSpeakerBinding>(),
    UserSpeakerContract.View {


    @InjectPresenter
    lateinit var presenter: UserSpeakerPresenter


    @Inject
    lateinit var presenterProvider: Provider<UserSpeakerPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSpeakerPresenter = presenterProvider.get().apply {
        val args =
            UserSpeakerFragmentArgs.fromBundle(requireArguments())
        memberId = args.userId
        eventId = args.eventId
    }

    private val mainDataSection = Section()
    private val labelSection = Section()
    private val subEventsDataSection = Section()


    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(mainDataSection)
        add(labelSection)
        add(subEventsDataSection)
    }

    private val onSubEventClickListener = object : EventActivityItem.OnEventActivityClickListener {

        override fun onSubEventClick(eventId: String, subEvent: EventActivityModel) {
            presenter.onSubEventClick(subEvent)
        }

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            presenter.onAddToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            presenter.onRemoveFromScheduleClick(subEvent)

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            listSpeakersContent.apply {
                this.adapter = groupAdapter
                onScrolled { dx, dy ->
                    presenter.changeAppBarElevation(dy)
                }
//                setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//                    mDy += scrollY - oldScrollY
//                    val mElevation = abs(mDy / 10f)
//                    appBar.apply {
//                        elevation = if (mElevation <= 10f) {
//                            mElevation
//                        } else {
//                            10f
//                        }
//                    }
//                }
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            ivAddToFavorite.setOnClickListener {
                presenter.onAddSpeakerToFavoriteClick(presenter.getUserDetailId())
            }
            btnGoToProfile.setOnClickListener {
                presenter.onGoToProfileClick()
            }
        }


    }

    override fun changeAppbarElevation(value: Float) {
        mBinding.appBar.apply {
            elevation = if (value <= 10f) {
                value
            } else {
                10f
            }
        }
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(
            UserSpeakerFragmentDirections.userToChat(userName, chatId)
                .apply {
                    setUserAvatar(userAvatar)
                })
    }


    override fun setSpeakersMainInfo(speaker: MemberModel, isCurrentUser: Boolean) {
        mBinding.apply {
            if (speaker.binds?.user?.state?.isRegistered == true) {
                if (!isCurrentUser) {
                    ivAddToFavorite.isVisible = true
                } else {
                    toolbarContent.setPadding(0, 0, 15.dp, 0)
                }
                btnGoToProfile.isVisible = true
            } else {
                ivAddToFavorite.isVisible = false
                btnGoToProfile.isVisible = false
            }
        }


        mainDataSection.update(
            listOf(
                UserSpeakerMainInfoItem(
                    presenter.isCurrentUser(),
                    speaker.binds?.user,
                    speaker.binds?.user?.nameLastName ?: "",
                    speaker.binds?.user?.address?.city ?: "",
                    speaker.organizationAndPosition,
                    speaker.binds?.user?.image?.uri,
                    speaker.status ?: "",
                    speaker.binds?.user?.state?.isRegistered ?: false
                ) {
                    presenter.onWriteMessageClick(it)
                }
            )
        )
    }

    override fun setSpeakerActivities(
        canShow: Boolean,
        data: Map<String?, List<EventActivityModel>>?
    ) {
        subEventsDataSection.add(EventDetailBlocksLabelItem(getString(R.string.speakers_activities_label)))
        if (!data.isNullOrEmpty()) {
            data.forEach {
                subEventsDataSection.add(
                    EventDetailActivitiesBlock(
                        presenter.eventId,
                        canShow,
                        it.key ?: "",
                        it.value,
                        onSubEventClickListener
                    )
                )
            }
        } else {
            subEventsDataSection.add(NoSubEventItem(getString(R.string.no_activity_title)))
        }
    }


    override fun setEmptyMainDataPlaceholder() {
        mainDataSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.SPEAKER_MAIN)))
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        val idLong = subEvent.id?.toLong()
        subEventsDataSection.findItemBy<EventActivityItem> { it.id == idLong }
            ?.notifyChanged(subEvent)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubEventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subEvent_fragment, args)
    }

    override fun showUserProfile(userId: String) {
        findNavController().navigate(
            UserSpeakerFragmentDirections.speakerToUser(
                userId
            )
        )
    }

    override fun showSpeakerAddedToFavoriteMessage() {
        EventAddedToFavoriteDialog(requireContext())
    }

    override fun updateSpeaker(speaker: UserDetail) {
        mBinding.apply {
            if (speaker.binds?.userFavorite == null) {
                ivAddToFavorite.setImageResource(R.drawable.ic_star)
            } else {
                ivAddToFavorite.setImageResource(R.drawable.ic_star_filled)
            }
        }

    }


    override fun layout(): Int = R.layout.fragment_user_speaker
}