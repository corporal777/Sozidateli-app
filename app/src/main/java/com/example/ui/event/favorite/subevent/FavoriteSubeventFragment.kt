package com.example.ui.event.favorite.subevent

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SubEvent
import com.example.holders.DayHeaderItem
import com.example.holders.SubEventItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_contacts.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteSubeventFragment : BaseFragment(), FavoriteSubeventContract.View, ToolbarFragment {

    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: FavoriteSubeventPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteSubeventPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteSubeventPresenter = presenterProvider.get().apply {
        actions = FavoriteSubeventFragmentArgs.fromBundle(arguments!!).actions.asList()
    }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun setData(data: Map<Long?, List<SubEvent>>) {
        val groups = mutableListOf<Group>()
        data.forEach { entry ->
            val date = entry.key
            val events = entry.value
            if (date != null) {
                groups.add(DayHeaderItem(date))
                events.forEach {
                    groups.add(SubEventItem(it, object : SubEventItem.OnSubEventClickListener {
                        override fun onSubEventClick(subEvent: SubEvent) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onAddToScheduleClick(subEvent: SubEvent) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onRemoveFromScheduleClick(subEvent: SubEvent) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    }))
                }
            }
        }
        groupAdapter.update(groups)
    }

    override fun layout() = R.layout.layout_list
}
