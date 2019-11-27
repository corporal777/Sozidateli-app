package com.example.ui.event.about

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.extensions.dp
import com.example.holders.EventInfoHeaderItem
import com.example.holders.EventPageItem
import com.example.holders.EventPartnerItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_about_event.*
import kotlinx.android.synthetic.main.fragment_about_event.swipeToRefresh
import kotlinx.android.synthetic.main.fragment_search.recyclerView
import kotlinx.android.synthetic.main.item_action_button.view.*
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class AboutEventFragment : BaseFragment(), AboutEventContract.View, ToolbarFragment {

    override val title: String
        get() = ""

    @InjectPresenter
    lateinit var presenter: AboutEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutEventPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenter = presenterProvider.get().apply {
        eventId = AboutEventFragmentArgs.fromBundle(arguments!!).eventId
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        spanCount = 12
    }

    private var eventHeaderItem: EventInfoHeaderItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter

            layoutManager = GridLayoutManager(context, groupAdapter.spanCount).apply {
                spanSizeLookup = groupAdapter.spanSizeLookup
            }

            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    private val padding = 16.dp

                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        if (parent.getChildViewHolder(view).itemViewType == R.layout.item_event_partner) {
                            val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
                            val gridLayoutManager = parent.layoutManager as GridLayoutManager
                            val spanSize = layoutParams.spanSize.toFloat()
                            val totalSpanSize = gridLayoutManager.spanCount.toFloat()

                            val n = totalSpanSize / spanSize // num columns
                            val c = layoutParams.spanIndex / spanSize // column index

                            val leftPadding = padding * ((n - c) / n)
                            val rightPadding = padding * ((c + 1) / n)

                            outRect.top = padding
                            outRect.left = leftPadding.toInt()
                            outRect.right = rightPadding.toInt()
                        }
                    }
                })
            }
        }

        btnRegister.btnAction.apply {
            text = getString(R.string.go_to_event)
            setOnClickListener { presenter.onGoToEventClick() }
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setEventData(
            logo: String?,
            organizationName: String?,
            dates: String?,
            description: String?,
            pages: List<EventPage>,
            partners: List<EventParther>,
            showContacts: Boolean
    ) {
        groupAdapter.update(listOf(
                EventInfoHeaderItem(-100L, logo, organizationName, dates, description) { iv, url ->
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            Pair(iv, iv.transitionName)
                    )

                    findNavController().navigate(
                            R.id.image_view_activity,
                            ImageViewActivityArgs.Builder(url, null, null, iv.transitionName).build().toBundle(),
                            null,
                            ActivityNavigatorExtras(options)
                    )
                }.apply {
                    eventHeaderItem = this
                },
                Section().apply {
                    if (showContacts) add(EventPageItem(-90, getString(R.string.about_event_contacts)) { presenter.onContactsClick() })
                    add(EventPageItem(-80, getString(R.string.about_event_speakers)) { presenter.onSpeakersClick() }.apply {
                        hasBottomPadding = pages.isNotEmpty()
                    })
                    addAll(pages.map { EventPageItem(it.id, it.menu) { presenter.onPageClick(it) } })
                },
                Section().apply {
                    addAll(partners.map { EventPartnerItem(it.id, it.logo, it.name) { presenter.onPartnerClick(it) } })
                }
        ))
        swipeToRefresh.isRefreshing = false
    }

    override fun setEventName(name: String) {
        toolbarContentActionBar?.title = name
    }

    override fun showRegisterButton(show: Boolean) {
        btnRegister.isVisible = show
        recyclerView.updatePadding(bottom = if (show) with(resources) {
            getDimensionPixelSize(R.dimen.action_button_height) + getDimensionPixelSize(R.dimen.action_button_margin_vertical) * 2
        } else 20.dp)
    }

    override fun showPage(eventId: String, pageId: String) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToPageFragment(eventId, pageId))
    }

    override fun showDocuments(eventId: String, pageId: String) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToDocumentsListFragment(eventId, pageId))
    }

    override fun showPartner(eventId: String, partnerId: String) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToPartnerFragment(eventId, partnerId))
    }

    override fun showSpeakers(eventId: String) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToSpeakersListFragment(eventId))
    }

    override fun showContacts(eventName: String, phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?, mapInfo: MapInfo?, places: List<Place>?) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToContactsFragment(
                eventName,
                phones.toTypedArray(),
                emails.toTypedArray(),
                webLinks.toTypedArray(),
                socialLinks.toTypedArray(),
                address,
                mapInfo,
                places?.toTypedArray()
        ))
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showLogoImage(url: String) {

    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_about_event
}
