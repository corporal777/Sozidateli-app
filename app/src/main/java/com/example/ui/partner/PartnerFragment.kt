package com.example.ui.partner

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Partner
import com.example.data.models.PartnerModel
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import kotlinx.android.synthetic.main.fragment_partner.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class PartnerFragment : BaseFragment(), PartnerContract.View, ToolbarFragment {
    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: PartnerPresenter

    @Inject
    lateinit var presenterProvider: Provider<PartnerPresenter>

    @ProvidePresenter
    fun providePresenter(): PartnerPresenter = presenterProvider.get().apply {
        PartnerFragmentArgs.fromBundle(requireArguments()).apply {
            dataEventId = eventId
            dataPartnerId = partnerId
        }
    }

    override fun setData(partner: PartnerModel, logo: Bitmap?, background: Bitmap?) {
        ivBackground.apply {
            clipToOutline = true
            if (background == null) {
                isVisible = false
            } else {
                setImageBitmap(background)
                setOnImageClickListener(this, partner.image?.uri)
            }
        }
        ivLogo.apply {
            clipToOutline = true
            if (logo == null) {
                isVisible = false
            } else {
                setImageBitmap(logo)
                setOnImageClickListener(this, partner.logo?.uri)
            }
        }

        tvName.apply {
            isVisible = partner.name?.isNotEmpty() == true
            text = partner.name
        }

        tvDescription.apply {
            isVisible = !partner.description.isNullOrEmpty()
            text = partner.description
        }

        //val link = partner.web?.takeIf { it.isNotBlank() }
        val link = partner.site?.joinToString("\n") { it.value?: "" }

        tvLinksTitle.isVisible = link != null

        tvLinks.apply {
            isVisible = link != null
            text = link
            removeUrlUnderline()
        }

        llSupportType.isVisible = !partner.supportType.isNullOrEmpty()
        tvSupportType.text = partner.supportType

        llContent.isVisible = true
    }

    private fun setOnImageClickListener(imageView: ImageView, url: String?) {
        imageView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair(it, it.transitionName)
            )

            findNavController().navigate(
                    R.id.image_view_activity,
                    ImageViewActivityArgs.Builder(url, null, null, it.transitionName).build().toBundle(),
                    null,
                    ActivityNavigatorExtras(options)
            )
        }
    }

    override fun layout() = R.layout.fragment_partner
}
