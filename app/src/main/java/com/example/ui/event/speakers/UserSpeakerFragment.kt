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
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.blocks.EventDetailBlocksLabelItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.speakers.new.SpeakersActivitiesGroup
import com.example.ui.event.speakers.new.UserSpeakerMainInfoItem
import com.example.ui.user.UserFragmentDirections
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_subevent.*
import kotlinx.android.synthetic.main.fragment_user_speaker.*
import kotlinx.android.synthetic.main.fragment_user_speaker.ivBack
import javax.inject.Inject
import javax.inject.Provider

class UserSpeakerFragment : BaseFragment(), UserSpeakerContract.View {


    @InjectPresenter
    lateinit var presenter: UserSpeakerPresenter

    private var userId = 0
    private var mDy: Int = 0

    @Inject
    lateinit var presenterProvider: Provider<UserSpeakerPresenter>

    @ProvidePresenter
    fun providePresenter(): UserSpeakerPresenter = presenterProvider.get().apply {
        val args = UserSpeakerFragmentArgs.fromBundle(requireArguments())
        userId = args.userId
        this@UserSpeakerFragment.userId = userId.toInt()
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_speakers_content.apply {
            this.adapter = groupAdapter
            setHasFixedSize(true)
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                mDy += scrollY - oldScrollY
                shadow.apply {
                    isVisible = mDy >= 60
                    alpha = 1f
                    animate().setDuration(500).alpha(1.0f)
                }
            }
        }

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnAddToFavorite.setOnClickListener {
            presenter.onAddSpeakerToFavoriteClick(userId.toString())
        }
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(
            UserSpeakerFragmentDirections.userToChat(userName, chatId).apply {
                setUserAvatar(userAvatar)
            })
    }


    override fun showSpeakerMainInfo(speaker: UserDetail) {
        if (speaker.binds?.userFavorite == null) {
            btnAddToFavorite.text = getString(R.string.add_to_favorites)
        } else {
            btnAddToFavorite.text = getString(R.string.delete_from_favorites)
        }

        mainDataSection.update(
            listOf(
                UserSpeakerMainInfoItem(
                    speaker,
                    speaker.fullName,
                    speaker.address?.city,
                    speaker.binds?.organization?.get(0)?.binds?.member?.get(0)?.position?.value,
                    speaker.image.uri
                ) {
                    presenter.onWriteMessageClick(it)
                }
            )
        )
    }

    override fun setSpeakerActivities(data: List<EventActivityModel>) {
        if (data.isNullOrEmpty()) {
            //subEventsDataSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.EVENT)))
        } else {
            //labelSection.update(listOf(EventDetailBlocksLabelItem("События спикера")))
            subEventsDataSection.update(
                listOf(
                    EventDetailBlocksLabelItem("События спикера"),
                    SpeakersActivitiesGroup(userId, data)
                )
            )
        }
    }

    override fun setEmptyEventsPlaceholder() {
        //subEventsDataSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.SUB_EVENT)))
    }

    override fun setEmptyMainDataPlaceholder() {
        mainDataSection.update(listOf(PlaceholderItem(PlaceholderItem.Type.SPEAKER)))
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