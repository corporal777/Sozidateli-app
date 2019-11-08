package com.example.ui.event.list.my

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.interfaces.ToolbarFragment
import com.example.ui.event.list.EventListFragment
import javax.inject.Inject
import javax.inject.Provider

class MyEventsFragment : EventListFragment<MyEventsPresenter>(), MyEventsContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.tab_events_title)

    @InjectPresenter(type = PresenterType.WEAK, tag = "MyEventsPresenter")
    override lateinit var presenter: MyEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "MyEventsPresenter")

    fun providePresenter(): MyEventsPresenter = presenterProvider.get()

    override fun selectEvent(event: Event) {
        findNavController().apply {
//            graph.startDestination = R.id.event_tabs_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.event_list_fragment, true)
                    .build()
            navigate(R.id.event_tabs_fragment, null, opts)
        }
    }
}
