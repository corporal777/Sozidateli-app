package com.example.ui.organizations

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.Organization
import com.example.data.models.user.User
import com.example.holders.EventItem
import com.example.holders.OrganizationUserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton
import com.example.util.ARG_EVENT
import com.rd.animation.type.AnimationType
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_organization.*
import kotlinx.android.synthetic.main.fragment_organization.btnAction
import kotlinx.android.synthetic.main.fragment_organization.llContent
import kotlinx.android.synthetic.main.fragment_status.scrollContainer
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
    }

    override fun setOrganization(organization: Organization, events: List<Event>, eventsTotal: Int, users: List<User>, usersTotal: Int, listsLimit: Int) {
        ivBackground.apply {
            if (organization.bg_image.isNullOrEmpty()) {
                isVisible = false
            } else {
                Picasso.get().load(organization.bg_image).into(this, object : Callback {
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
            if (organization.logo.isNullOrEmpty()) {
                isVisible = false
            } else {
                Picasso.get().load(organization.logo)
                        .noFade()
                        .into(this, object : Callback {
                            override fun onSuccess() {
                                logoBorder.isVisible = true
                            }

                            override fun onError(e: java.lang.Exception?) {
                                logoBorder.isVisible = false
                                isVisible = false
                            }
                        })
            }
        }

        tvName.text = organization.name
        btnAction.apply {
            setAction(if (organization.isInFavorite == true) UserSubscribeButton.ACTION_UNSUBSCRIBE else UserSubscribeButton.ACTION_SUBSCRIBE)
            setOnClickListener { TODO() }
        }
        tvDescriptionShort.text = "Мотивируем сдавать кровь. Присоединяйся к крупнейшему сообществу доноров России и Ближнего Зарубежья"
        tvLinks.text = "https://organization.ru\n\nhttps://vk.com/antigaiandroid"
        tvAddress.text = "420025, респ.Татарстан, г.Казань, ул.Новый Татарстан, дом.14\n(904) 669-66-21\nsupport@donorsearch.org"
        tvDescription.text = "Некоммерческий фонд по оказанию помощи бездомным, брошенным животным, а так же животным - инвалидам. Хоспис, передержка и устройство в семьи."

        val usersCountText = usersTotal.toString()
        val peoplesText = getString(R.string.organization_peoples)
        val peoplesSpannable = "$peoplesText $usersCountText".toSpannable().apply {
            val typefaceSpan = CalligraphyTypefaceSpan(TypefaceUtils.load(resources.assets, "fonts/OpenSans-Bold.ttf"))
            val start = peoplesText.length + 1
            val end = start + usersCountText.length
            set(start, end, typefaceSpan)
        }
        tvPeoples.text = peoplesSpannable

        rvPeoples.adapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(users.map { OrganizationUserItem(it.user_id, it.fullName, it.user_avatar, it.user_description) { TODO() } })
        }

        btnPeoples.apply {
            isVisible = usersTotal > listsLimit
            setOnClickListener { TODO() }
        }

        dividerPeoples.isVisible = btnPeoples.isVisible

        val eventsCountText = eventsTotal.toString()
        val eventsText = getString(R.string.organization_events)
        val eventsSpannable = "$eventsText $eventsCountText".toSpannable().apply {
            val typefaceSpan = CalligraphyTypefaceSpan(TypefaceUtils.load(resources.assets, "fonts/OpenSans-Bold.ttf"))
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
            isVisible = eventsTotal > listsLimit
            setOnClickListener { presenter.onShowMoreEventsClick() }
        }

        llContent.isVisible = true
    }

    override fun showEvents(id: String) {
        findNavController().navigate(OrganizationFragmentDirections.organizationToOrganizationEvents(id))
    }

    override fun showAboutEvent(event: Event) {
        findNavController().navigate(R.id.about_event, bundleOf(ARG_EVENT to event))
    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, bundleOf(ARG_EVENT to event))
    }

    override fun changeScrollY(scroll: Int) {
        scrollContainer.scrollTo(0, scroll)
    }

    override fun layout() = R.layout.fragment_organization
}
