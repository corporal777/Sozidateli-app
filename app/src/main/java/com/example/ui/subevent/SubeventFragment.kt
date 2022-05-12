package com.example.ui.subevent

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.extensions.findItemBy
import com.example.holders.ListSectionNameItem
import com.example.holders.SpeakerGroup
import com.example.holders.SubeventInfoItem
import com.example.holders.UserItem
import com.example.holders.redesign.ScreenHeaderItem
import com.example.holders.redesign.blocks.EventDetailBlocksLabelItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.speakers.UserSpeakerFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.toolbar.widget.OnTransparentListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_about_event_new.*
import kotlinx.android.synthetic.main.fragment_about_event_new.shadow
import kotlinx.android.synthetic.main.fragment_subevent.*
import kotlinx.android.synthetic.main.fragment_subevent.ivBack
import kotlinx.android.synthetic.main.fragment_user_speaker.*
import javax.inject.Inject
import javax.inject.Provider

class SubeventFragment : BaseFragment(), SubeventContract.View {


    @InjectPresenter
    lateinit var presenter: SubeventPresenter

    private var mEventId = ""
    private var mSubEventId = ""
    private var mDy: Int = 0

    @Inject
    lateinit var presenterProvider: Provider<SubeventPresenter>

    @ProvidePresenter
    fun providePresenter(): SubeventPresenter = presenterProvider.get().apply {
        val args = SubeventFragmentArgs.fromBundle(requireArguments())
        event = args.eventId
        subevent = args.subeventId
        mEventId = event
        mSubEventId = subevent
    }

    private val headerSection = Section()
    private val infoSection = Section()
    private val speakersSection by lazy {
        Section().apply {
            setHeader(EventDetailBlocksLabelItem( getString(R.string.speakers)).apply {

            })
            setHideWhenEmpty(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                add(headerSection)
                add(infoSection)
                add(speakersSection)
            }
           setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
               mDy += scrollY - oldScrollY
               toolbar_shadow.apply {
                   isVisible = mDy >= 60
               }
           }
        }

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun setData(subEvent: EventActivityModel) {
        //infoSection.update(listOf(SubeventInfoItem(subEvent) { presenter.onSubeventChangeSubscriptionClick(subEvent) }))
        headerSection.update(listOf(ScreenHeaderItem(getString(R.string.event))))

        infoSection.update(listOf(SubeventInfoItem(subEvent, {
            showToast("Added")
            presenter.onAddToScheduleClick(it)
        }, {
            showToast("Removed")
            presenter.onRemoveFromScheduleClick(it)
        }, {
           showToast(it.toString())
        })))
    }

    override fun setSpeakers(speakers: List<MemberModel>) {
        speakersSection.update(speakers.map { speaker ->
            SpeakerGroup(speaker, { presenter.onSpeakerClick(it) }, { presenter.onSpeakerChangeSubscriptionClick(it) })
        })
    }

    override fun showSpeakerProfile(speaker: MemberModel) {
        findNavController().navigate(
            R.id.user_speaker_fragment,
            UserSpeakerFragmentArgs.Builder(speaker.user.toString(), mEventId).build().toBundle()
        )
        //findNavController().navigate(R.id.user_fragment, UserFragmentArgs.Builder(speaker.user.toString()).build().toBundle())
    }

    override fun updateSpeaker(speaker: MemberModel) {
        val idLong = speaker.user?.toLong()
        for (i in 0 until speakersSection.itemCount) {
            val item = speakersSection.getItem(i)
            if (item.id == idLong && item is UserItem) {
                item.notifyChanged(speaker.binds?.user?.getUserSubscribeAction())
                break
            }
        }
    }

    override fun updateSubevent(subEvent: EventActivityModel) {
        val idLong = subEvent?.id?.toLong()
        infoSection.findItemBy<SubeventInfoItem> { it -> it.id == idLong }?.notifyChanged()
    }

    override fun onStart() {
        super.onStart()
        if (recyclerView != null){
            mDy += recyclerView.scrollY
        }
    }

    override fun layout() = R.layout.fragment_subevent
}
