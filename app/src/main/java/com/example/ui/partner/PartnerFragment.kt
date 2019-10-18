package com.example.ui.partner

import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Partner
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_partner.*
import javax.inject.Inject
import javax.inject.Provider

class PartnerFragment : BaseFragment(), PartnerContract.View, ToolbarFragment {
    override val title: String
        get() = getString(R.string.partners_label)

    @InjectPresenter
    lateinit var presenter: PartnerPresenter

    @Inject
    lateinit var presenterProvider: Provider<PartnerPresenter>

    @ProvidePresenter
    fun providePresenter(): PartnerPresenter = presenterProvider.get().apply {
        PartnerFragmentArgs.fromBundle(arguments!!).apply {
            dataEventId = eventId
            dataPartnerId = partnerId
        }
    }

    private var toolbarContentActionBar: ToolbarContentActionBar? = null

    override fun setData(partner: Partner) {
        ivBackground.apply {
            if (partner.background.isNullOrEmpty()) {
                isVisible = false
            } else {
                Picasso.get().load(partner.background).into(this, object : Callback {
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
            if (partner.logo.isNullOrEmpty()) {
                isVisible = false
            } else {
                Picasso.get().load(partner.logo)
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

        tvName.apply {
            isVisible = !partner.name.isEmpty()
            text = partner.name
        }
        tvDescription.apply {
            isVisible = !partner.description.isNullOrEmpty()
            text = partner.description
        }
        tvLinks.apply {
            isVisible = !partner.web.isNullOrEmpty()
            text = partner.web
        }

        llSupportType.isVisible = !partner.typeSupport.isNullOrEmpty()
        tvSupportType.text = partner.typeSupport

        llContent.isVisible = true
    }

    override fun setTitle(title: String) {
        toolbarContentActionBar?.title = title
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun layout() = R.layout.fragment_partner
}
