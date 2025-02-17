package com.example.ui.partner

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentPartnerBinding
import com.example.data.models.PartnerModel
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.partner.items.PartnerMainInfoItem
import com.example.ui.views.toolbar.ToolbarContent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class PartnerFragment : BaseVBFragment<FragmentPartnerBinding>(), PartnerContract.View,
    ToolbarFragment {

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

    private val mainDataSection by lazy {
        Section().apply {
            setPlaceholder(PlaceholderItem(PlaceholderItem.Type.ORGANIZATION_MAIN))
        }
    }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(mainDataSection)
        }
    }

    private lateinit var toolbarContent: ToolbarContent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            partnerList.adapter = groupAdapter
            swipeToRefresh.setOnRefreshListener {
                presenter.onRefreshRequest()
            }
        }
    }

    override fun setData(partner: PartnerModel) {
        toolbarContent.setToolbarTitle(partner.name ?: "")
        mainDataSection.updateItem(
            PartnerMainInfoItem(
                partner.id,
                partner.loadPartnerLogo(),
                partner.loadPartnerImage(),
                partner.name,
                partner.description,
                partner.site?.joinToString("\n") { it.value ?: "" },
                partner.supportType
            ) { imageView, url ->
                showPartnerImage(imageView, url)
            }
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    private fun showPartnerImage(imageView: ImageView, url: String?) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(imageView, imageView.transitionName)
        )

        findNavController().navigate(
            R.id.image_view_activity,
            ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build()
                .toBundle(),
            null,
            ActivityNavigatorExtras(options)
        )
    }

    override fun binding() = FragmentPartnerBinding::class.java
    override fun layout() = R.layout.fragment_partner
    override val title: CharSequence = ""
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
    }
}
