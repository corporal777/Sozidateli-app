package com.example.ui.organizations

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.Organization
import com.example.data.models.OrganizationMember
import com.example.holders.EventItem
import com.example.holders.OrganizationUserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.UserSubscribeButton
import com.rd.animation.type.AnimationType
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_organization.*
import kotlinx.android.synthetic.main.fragment_organization.btnAction
import kotlinx.android.synthetic.main.fragment_organization.llContent
import kotlinx.android.synthetic.main.fragment_status.scrollContainer
import parseColor
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils
import javax.inject.Inject
import javax.inject.Provider

class OrganizationFragment : BaseFragment(), OrganizationContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = ""

    @InjectPresenter
    lateinit var presenter: OrganizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationPresenter = presenterProvider.get().apply {
        organizationId = OrganizationFragmentArgs.fromBundle(arguments!!).organizationId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollContainer.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            presenter.onScrollPositionChange(scrollY)
        }

        llContent.isVisible = false

        btnAction.apply {
            setOnClickListener {
                when (action) {
                    UserSubscribeButton.Action.SUBSCRIBE -> presenter.onSubscribeClick()
                    UserSubscribeButton.Action.UNSUBSCRIBE -> presenter.onUnsubscribeClick()
                    else -> throw IllegalArgumentException("Wrong action: $it for organization")
                }
            }
        }
    }

    override fun setOrganization(organization: Organization, events: List<Event>, users: List<OrganizationMember>) {
        ivBackground.apply {
            if (organization.background.isNullOrEmpty()) {
                isVisible = false
            } else {
                Picasso.get().load(organization.background).into(this, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        isVisible = false
                    }
                })
            }
        }
        ivLogo.apply {
            clipToOutline = true
            Picasso.get().load(organization.logo)
                    .noFade()
                    .into(this, object : Callback {
                        override fun onSuccess() {
                            setOnClickListener {
                                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        requireActivity(),
                                        Pair(it, it.transitionName)
                                )

                                findNavController().navigate(
                                        R.id.image_view_activity,
                                        ImageViewActivityArgs.Builder(organization.logo, null, null, it.transitionName).build().toBundle(),
                                        null,
                                        ActivityNavigatorExtras(options)
                                )
                            }
                        }

                        override fun onError(e: java.lang.Exception?) {

                        }
                    })
        }

        tvOrganizationImageName.apply {
            text = organization.name
            clipToOutline = true
            ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(organization.backgroundColor.parseColor()
                    ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)))
        }

        tvName.text = organization.name

        tvDescriptionShort.text = organization.descriptionShort

        val linksData = StringBuilder().apply {
            organization.webLinks?.let { list ->
                if (list.isNotEmpty()) append(list.joinToString(separator = "\n"))
            }
            organization.socialLinks?.let { list ->
                if (list.isNotEmpty()) {
                    if (length > 0) append("\n\n")
                    append(list.joinToString(separator = "\n"))
                }
            }
        }
        tvLinks.apply {
            isVisible = linksData.isNotEmpty()
            text = linksData
        }

        val addressData = StringBuilder().apply {
            organization.address?.let { append(it) }
            organization.phones?.let { list ->
                if (list.isNotEmpty()) {
                    if (length > 0) append("\n")
                    append(list.joinToString(separator = "\n") { if (!it.affiliation.isNullOrBlank()) "${it.affiliation}: ${it.phone}" else it.phone })
                }
            }
            organization.emails?.let { list ->
                if (list.isNotEmpty()) {
                    if (length > 0) append("\n")
                    append(list.joinToString(separator = "\n") { if (!it.affiliation.isNullOrBlank()) "${it.affiliation}: ${it.email}" else it.email })
                }
            }
        }

        tvAddress.apply {
            isVisible = addressData.isNotEmpty()
            text = addressData
        }

        tvDescription.apply {
            isVisible = !organization.descriptionFull.isNullOrEmpty()
            text = organization.descriptionFull
        }

        val usersCountText = "${organization.totalMembers}"
        val peoplesText = getString(R.string.organization_peoples)
        val peoplesSpannable = "$peoplesText $usersCountText".toSpannable().apply {
            val typefaceSpan = CalligraphyTypefaceSpan(TypefaceUtils.load(resources.assets, "fonts/Roboto-Bold.ttf"))
            val start = peoplesText.length + 1
            val end = start + usersCountText.length
            set(start, end, typefaceSpan)
        }
        tvPeoples.text = peoplesSpannable

        rvPeoples.adapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(users.map {
                val user = it.user
                OrganizationUserItem(it.id, user.fullName, user.user_avatar, it.position) { presenter.onUserClick(user) }
            })
        }

        btnPeoples.apply {
            isVisible = organization.totalMembers > users.size
            setOnClickListener { presenter.onShowMoreUsersClick() }
        }

        dividerPeoples.isVisible = btnPeoples.isVisible

        val eventsCountText = "${organization.totalEvents}"
        val eventsText = getString(R.string.organization_events)
        val eventsSpannable = "$eventsText $eventsCountText".toSpannable().apply {
            val typefaceSpan = CalligraphyTypefaceSpan(TypefaceUtils.load(resources.assets, "fonts/Roboto-Bold.ttf"))
            val start = eventsText.length + 1
            val end = start + eventsCountText.length
            set(start, end, typefaceSpan)
        }
        tvEvents.text = eventsSpannable

        vpEvents.apply {
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                addAll(events.map {
                    EventItem(
                            it,
                            { presenter.onEventClick(it) },
                            { presenter.onGoToEventClick(it) }
                    ).apply { isInHorizontalParent = true }
                })
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    eventPageIndicator.onPageScrollStateChanged(state)
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    eventPageIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    eventPageIndicator.setSelected(position)
                }
            })
        }

        eventPageIndicator.apply {
            count = events.size
            setAnimationType(AnimationType.COLOR)
        }

        btnEvents.apply {
            isVisible = organization.totalEvents > events.size
            setOnClickListener { presenter.onShowMoreEventsClick() }
        }

        llContent.isVisible = true
    }

    override fun setSubscribed(isSubscribed: Boolean) {
        btnAction.apply {
            setAction(if (isSubscribed) UserSubscribeButton.Action.UNSUBSCRIBE else UserSubscribeButton.Action.SUBSCRIBE)
        }
    }

    override fun showEvents(organizationId: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToOrganizationEvents(organizationId))
    }

    override fun showAboutEvent(event: Event) {
        findNavController().navigate(R.id.about_event, AboutEventFragmentArgs.Builder(event.id).build().toBundle())
    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event.id).build().toBundle())
    }

    override fun showUsers(organizationId: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToOrganizationUsers(organizationId))
    }

    override fun showUser(id: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToUser(id))
    }

    override fun changeScrollY(scroll: Int) {
        scrollContainer.scrollTo(0, scroll)
    }

    override fun layout() = R.layout.fragment_organization
}
