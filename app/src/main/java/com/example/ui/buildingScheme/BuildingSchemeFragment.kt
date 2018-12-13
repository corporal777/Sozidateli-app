package com.example.ui.buildingScheme

import android.os.Bundle
import android.support.transition.Fade
import android.support.v4.widget.NestedScrollView
import android.view.View
import androidx.navigation.fragment.FragmentNavigatorExtras
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Place
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.ui.image.ImageViewFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_building_scheme.*
import javax.inject.Inject
import javax.inject.Provider

class BuildingSchemeFragment : BaseNestedNavigationFragment(), BuildingSchemeContract.View {

    @InjectPresenter
    lateinit var presenter: BuildingSchemePresenter

    @Inject
    lateinit var presenterProvider: Provider<BuildingSchemePresenter>

    @ProvidePresenter
    fun providePresenter(): BuildingSchemePresenter = presenterProvider.get()

    init {
        exitTransition = Fade()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollContainer.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            presenter.onScrollPositionChange(scrollY)
        }
    }

    override fun setPlaceData(place: Place) {
        ivScheme.apply {
            Picasso.get().load(place.schemeImage).into(this)
            setOnClickListener { presenter.onImageClick() }
        }

        tvScheme.apply {
            text = place.scheme
        }
    }

    override fun showImage(url: String) {
        findParentNavigation().navigate(
                R.id.image_view_fragment,
                bundleOf(ImageViewFragment.ARG_IMAGE_URL to url),
                null,
                FragmentNavigatorExtras(ivScheme to "image")
        )
    }

    override fun changeScrollY(scroll: Int) {
        scrollContainer.scrollTo(0, scroll)
    }

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_building_scheme
}
