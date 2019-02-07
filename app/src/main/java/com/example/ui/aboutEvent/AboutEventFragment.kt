package com.example.ui.aboutEvent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_about_event.*
import kotlinx.android.synthetic.main.item_about_event.view.*
import setDatesIntervalText
import javax.inject.Inject
import javax.inject.Provider

class AboutEventFragment : BaseFragment(), AboutEventContract.View {

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
        Picasso.get().load(event.logo).placeholder(R.drawable.ic_launcher).into(ivLogo)
        tvOrganizationLabel.text = event.organizationName
        tvEventLabel.text = event.name
        tvEventDate.setDatesIntervalText(event.start, event.finish)

        btnGoToEvent.apply {
            visibility = View.VISIBLE
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

    override fun showTransfer(event: Event) {

    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment)
    }

    override fun setLabel(label: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = label
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_about_event
}
