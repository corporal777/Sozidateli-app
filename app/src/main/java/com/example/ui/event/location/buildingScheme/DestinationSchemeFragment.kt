package com.example.ui.event.location.buildingScheme

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.Place
import com.example.databinding.FragmentDestinationSchemeBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.main.MainActivity
import com.example.ui.views.toolbar.ToolbarContent
import com.google.android.material.tabs.TabLayoutMediator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_building_scheme.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class DestinationSchemeFragment : BaseFragment<FragmentDestinationSchemeBinding>(),
    DestinationSchemeContract.View, ToolbarFragment {


    @InjectPresenter
    lateinit var presenter: DestinationSchemePresenter

    @Inject
    lateinit var presenterProvider: Provider<DestinationSchemePresenter>

    @ProvidePresenter
    fun providePresenter(): DestinationSchemePresenter = presenterProvider.get().apply {
        eventId = DestinationSchemeFragmentArgs.fromBundle(requireArguments()).eventId
    }


    private val schemeSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(schemeSection)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewPager.apply {
                adapter = groupAdapter
            }
        }
    }

    override fun setScheme(places: List<Place>, page: Int) {
        mBinding.tabDots.apply {
            isVisible = places.size > 1
            TabLayoutMediator(this, mBinding.viewPager) { _, _ ->
            }.attach()
        }
        schemeSection.update(
            places.map {
                DestinationSchemeItem(
                    it.id,
                    it.name,
                    it.description,
                    it.image,
                    { image -> presenter.onImageClick(image) },
                    { s -> presenter.onScrollChangeOffset(s) }
                )
            }
        )
    }


    override fun showImage(url: String?) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(ivScheme, ivScheme.transitionName)
        )

        findNavController().navigate(
            R.id.image_view_activity,
            ImageViewActivityArgs.Builder(url, null, null, ivScheme.transitionName).build()
                .toBundle(),
            null,
            ActivityNavigatorExtras(options)
        )
    }

    override fun setAppBarShadow(value: Float) {
        (requireActivity() as MainActivity).setAppBarElevation(value)
    }

    override fun layout(): Int = R.layout.fragment_destination_scheme
    override val title: CharSequence by lazy { getString(R.string.scheme_of_building) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}