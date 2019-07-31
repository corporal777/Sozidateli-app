package com.example.ui.aboutEvent

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.Partner
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_about_event.*
import kotlinx.android.synthetic.main.item_about_event.view.*
import setDatesIntervalText
import javax.inject.Inject
import javax.inject.Provider

class AboutEventFragment : BaseFragment(), AboutEventContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: AboutEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutEventPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutEventPresenter = presenterProvider.get().apply {
        val data = arguments?.let { AboutEventFragmentArgs.fromBundle(it) }
        data?.event.let {
            if (it == null) setupWithUserEvent()
            else setEvent(it)
        }
    }

    override val title: String
        get() = AboutEventFragmentArgs.fromBundle(arguments!!).event!!.name!!

    init {
        val transition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutForum.tvLabel.apply {
            text = getString(R.string.about_event_about_forum)
            setOnClickListener { presenter.onAboutForumClick() }
        }

        news.tvLabel.apply {
            text = getString(R.string.about_event_news)
            setOnClickListener { presenter.onNewsClick() }
        }

        documents.tvLabel.apply {
            text = getString(R.string.about_event_documents)
            setOnClickListener { presenter.onDocumentsClick() }
        }

        contacts.tvLabel.apply {
            text = getString(R.string.about_event_contacts)
            setOnClickListener { presenter.onContactsClick() }
        }

        transfers.tvLabel.apply {
            text = getString(R.string.about_event_transfer)
            setOnClickListener { presenter.onTransferClick() }
        }

        btnGoToEvent.setOnClickListener { presenter.onGoToEventClick() }
    }

    override fun setEventData(event: Event) {
        tvOrganizationLabel.text = event.organization?.name
        tvEventLabel.text = event.name
        tvEventDate.setDatesIntervalText(event.conference_start, event.conference_finish)
        btnGoToEvent.visibility = View.VISIBLE

        Picasso.get().load(event.logo).placeholder(R.drawable.ic_launcher_background).into(ivLogo, object : Callback {
            override fun onSuccess() {
                presenter.onImageLoad()
                startPostponedEnterTransition()
            }

            override fun onError(e: Exception?) {
                presenter.onImageLoadError()
                startPostponedEnterTransition()
            }
        })
    }

    override fun setVisibleButtonGoToEvent(isVisible: Boolean) {
        btnGoToEvent.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setPartners(partners: List<Partner>) {
        tvLabelPartners.visibility = View.VISIBLE
        llPartnerContainer.visibility = View.VISIBLE

        val countSubContainer = Math.ceil(partners.size.toDouble() / 3.toDouble()).toInt()
        val listSubContainer = mutableListOf<LinearLayout>()

        for (i in 0 until countSubContainer) {
            val subContainer = LinearLayout(context)
            subContainer.orientation = LinearLayout.HORIZONTAL
            val partContainerLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            subContainer.layoutParams = partContainerLayoutParams

            listSubContainer.add(subContainer)
        }

        val maxHeightImage = context?.resources?.getDimensionPixelSize(R.dimen.max_height_image)
        var currentHeight = maxHeightImage

        partners.forEach {
            val index = partners.indexOf(it)

            val subContainer = listSubContainer[0]

            if (subContainer.childCount == 0) {
                var left = partners.size - index
                if (left > 3) left = 3
                currentHeight = maxHeightImage!! / left
            }


            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            val imageViewLayoutParams = LinearLayout.LayoutParams(0, currentHeight!!)
            imageViewLayoutParams.weight = 1F
            imageView.layoutParams = imageViewLayoutParams
            imageView.setOnClickListener { view -> presenter.onPartnerClick(it) }

            Picasso.get().load(it.logo.let { if (it.isNullOrEmpty()) null else it }).placeholder(R.drawable.ic_launcher_background).into(imageView)

            subContainer.addView(imageView)

            if ((index + 1) % 3 == 0 || index == partners.size - 1) {
                llPartnerContainer.addView(listSubContainer[0])
                listSubContainer.removeAt(0)
            }
        }
    }

    override fun showAboutForum(event: Event) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToAboutForumFragment(event))
    }

    override fun showNews(event: Event) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToNewsListFragment(event))
    }

    override fun showDocuments(event: Event) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToDocumentsListFragment(event))
    }

    override fun showContacts(event: Event) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToSpeakersListFragment(event))
    }

    override fun showPartner(partner: Partner) {
        findNavController().navigate(AboutEventFragmentDirections.actionAboutEventFragmentToPartnerFragment(partner))
    }

    override fun showTransfer(event: Event) {

    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, bundleOf("event" to event))
    }

    override fun layout() = R.layout.fragment_about_event
}
