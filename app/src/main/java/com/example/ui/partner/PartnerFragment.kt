package com.example.ui.partner

import android.graphics.Bitmap
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Partner
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.toolbar.ToolbarContentActionBar
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

    override fun setData(partner: Partner, logo: Bitmap?, background: Bitmap?) {
        ivLogo.apply {
            if (background == null) {
                isVisible = false
            } else {
                setImageBitmap(background)
            }
        }
        ivLogo.apply {
            if (logo == null) {
                isVisible = false
            } else {
                setImageBitmap(logo)
                setOnClickListener {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            Pair(this, this.transitionName)
                    )

                    findNavController().navigate(
                            R.id.image_view_activity,
                            ImageViewActivityArgs.Builder(partner.logo, null, null, this.transitionName).build().toBundle(),
                            null,
                            ActivityNavigatorExtras(options)
                    )
                }
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
