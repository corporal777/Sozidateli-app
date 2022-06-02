package com.example.ui.event.speakers

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
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.blocks.EventDetailBlocksLabelItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.speakers.new.SpeakersActivitiesGroup
import com.example.ui.event.speakers.new.UserSpeakerMainInfoItem
import com.example.ui.subevent.SubeventFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_user_speaker.*
import kotlinx.android.synthetic.main.fragment_user_speaker.ivBack
import javax.inject.Inject
import javax.inject.Provider

class UserSpeakerFragment : BaseFragment(), UserSpeakerContract.View {


    @InjectPresenter
    lateinit var presenter: UserSpeakerPresenter

    private var memberId = 0
    private var mDy: Int = 0

    @Inject
    lateinit var presenterProvider: Provider<UserSpeakerPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSpeakerPresenter = presenterProvider.get().apply {
        val args = UserSpeakerFragmentArgs.fromBundle(requireArguments())
        memberId = args.userId
        this@UserSpeakerFragment.memberId = memberId.toInt()
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
        override fun onActivityClick(subEvent: EventActivityModel) =
            presenter.onSubEventClick(subEvent)

        override fun onAddToScheduleClick(subEvent: EventActivityModel) =
            presenter.onAddToScheduleClick(subEvent)

        override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) =
            presenter.onRemoveFromScheduleClick(subEvent)

        override fun onUpdateScheduleState(subEvent: EventActivityModel) {}
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_speakers_content.apply {
            this.adapter = groupAdapter
            setHasFixedSize(true)
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                mDy += scrollY - oldScrollY
                if (mDy >= 30) {
                    userSpeakerBarLayout.elevation = 10f
                } else {
                    userSpeakerBarLayout.elevation = 0f
                }
            }
        }

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnAddToFavorite.setOnClickListener {
            presenter.onAddSpeakerToFavoriteClick(presenter.getUserDetailId())
        }
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(
            UserSpeakerFragmentDirections.userToChat(userName, chatId).apply {
                setUserAvatar(userAvatar)
            })
    }


    override fun showSpeakerMainInfo(speaker: MemberModel, isCurrentUser: Boolean) {
        if (speaker.binds?.user?.binds?.userFavorite == null) {
            btnAddToFavorite.text = getString(R.string.add_to_favorites)
        } else {
            btnAddToFavorite.text = getString(R.string.delete_from_favorites)
        }

        mainDataSection.update(
            listOf(
                UserSpeakerMainInfoItem(
                    presenter.isCurrentUser(),
                    speaker.binds?.user,
                    speaker.binds?.user?.fullName ?: "",
                    speaker.binds?.user?.address?.city,
                    speaker.description,
                    speaker.binds?.user?.image?.uri,
                    speaker.status ?: "",
                    speaker.binds?.user?.state?.isRegistered ?: false
                ) {
                    presenter.onWriteMessageClick(it)
                }
            )
        )
        if (!isCurrentUser){
            btnAddToFavorite.isVisible = true
        }

    }

    override fun setSpeakerActivities(data: List<EventActivityModel>) {
        subEventsDataSection.update(
            listOf(
                EventDetailBlocksLabelItem(getString(R.string.speakers_activities_label)),
                SpeakersActivitiesGroup(
                    getString(R.string.no_activity_title),
                    data,
                    onSubEventClickListener
                )
            )
        )
    }

    override fun setEmptyEventsPlaceholder() {
        //subEventsDataSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.SUB_EVENT)))
    }

    override fun setEmptyMainDataPlaceholder() {
        mainDataSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.SPEAKER_MAIN)))
    }

    override fun updateSubEvent(subEvent: EventActivityModel) {
        //subEventsBlock.findGroupBy<EventDetailActivitiesBlock> { true }?.updateButtonState(subEvent)
        val idLong = subEvent.id?.toLong()
        subEventsDataSection.findGroupBy<SpeakersActivitiesGroup> { true }
            ?.updateButtonState(subEvent)
    }

    override fun showSubEvent(eventId: String, subEventId: String) {
        val args = SubeventFragmentArgs.Builder(eventId, subEventId).build().toBundle()
        findNavController().navigate(R.id.subevent_fragment, args)
    }

    override fun updateSpeaker(speaker: UserDetail) {
        if (speaker.binds?.userFavorite == null) {
            btnAddToFavorite.text = getString(R.string.add_to_favorites)
        } else {
            btnAddToFavorite.text = getString(R.string.delete_from_favorites)
        }
    }

    override fun onStart() {
        super.onStart()
        if (list_speakers_content != null) {
            mDy += list_speakers_content.scrollY
        }
    }

    override fun layout(): Int = R.layout.fragment_user_speaker
}