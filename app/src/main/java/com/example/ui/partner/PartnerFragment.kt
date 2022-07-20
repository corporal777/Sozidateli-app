package com.example.ui.partner

import android.graphics.Bitmap
import android.os.Bundle
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
import com.example.data.models.PartnerModel
import com.example.databinding.FragmentPartnerBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.toolbar.SimpleTitleToolbar
import onScrolled
import removeUrlUnderline
import javax.inject.Inject
import javax.inject.Provider

class PartnerFragment : BaseFragmentNew<FragmentPartnerBinding>(), PartnerContract.View,
    SimpleTitleToolbar {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.scrollContainer.onScrolled { scrollY, oldScrollY, scrollX, oldScrollX ->
            presenter.changeAppBarElevation(scrollY - oldScrollY)
        }
    }

    override fun setData(partner: PartnerModel, logo: Bitmap?, background: Bitmap?) {
        mBinding.apply {
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
                setToolbarTitle(partner.name ?: "")
            }

            tvDescription.apply {
                isVisible = !partner.description.isNullOrEmpty()
                text = partner.description
            }

            //val link = partner.web?.takeIf { it.isNotBlank() }
            val link = partner.site?.joinToString("\n") { it.value ?: "" }

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
    }

    private fun setOnImageClickListener(imageView: ImageView, url: String?) {
        imageView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(it, it.transitionName)
            )

            findNavController().navigate(
                R.id.image_view_activity,
                ImageViewActivityArgs.Builder(url, null, null, it.transitionName).build()
                    .toBundle(),
                null,
                ActivityNavigatorExtras(options)
            )
        }
    }

    override fun layout() = R.layout.fragment_partner
}
