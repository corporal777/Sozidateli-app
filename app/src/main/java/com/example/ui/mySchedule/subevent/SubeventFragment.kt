package com.example.ui.mySchedule.subevent

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Subevent
import com.example.data.models.User
import com.example.holders.SpeakerItem
import com.example.holders.SubeventHeaderItem
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.ARG_USER
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.reactivex.internal.subscriptions.SubscriptionHelper
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_subevent.*
import javax.inject.Inject
import javax.inject.Provider

class SubeventFragment : BaseNestedNavigationFragment(), SubeventContract.View {

    @InjectPresenter
    lateinit var presenter: SubeventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SubeventPresenter>

    @ProvidePresenter
    fun providePresenter(): SubeventPresenter = presenterProvider.get().apply {
        val args = SubeventFragmentArgs.fromBundle(arguments)
        subevent = args.subevent
    }

    private val section = Section()
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        add(section)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = groupAdapter
            if(itemDecorationCount==0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }
    }

    override fun setData(subevent: Subevent, users: List<User>) {
        section.setHeader(SubeventHeaderItem(subevent,presenter))
        section.update(users.map {
            SpeakerItem(it,presenter)
        })
    }

    override fun showSpeakerProfile(user: User) {
        findParentNavigation().navigate(R.id.speaker_fragment,bundleOf(
                ARG_USER to user
        ))
    }

    override fun openUserList(subevent: Subevent) {
        findParentNavigation().navigate(SubeventFragmentDirections.subeventToUserList(subevent.id))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_subevent
}
