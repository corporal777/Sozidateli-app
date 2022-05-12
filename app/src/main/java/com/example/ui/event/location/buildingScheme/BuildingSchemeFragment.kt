package com.example.ui.event.location.buildingScheme

import android.content.res.Resources
import android.os.Bundle
import android.util.SparseIntArray
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import androidx.viewpager2.widget.ViewPager2
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimpleRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Place
import com.example.ui.base.BaseFragment
import com.example.ui.image.ImageViewActivityArgs
import com.rd.animation.type.AnimationType
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_building_scheme.*
import kotlinx.android.synthetic.main.item_building_scheme.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class BuildingSchemeFragment private constructor() : BaseFragment(), BuildingSchemeContract.View {

    @InjectPresenter
    lateinit var presenter: BuildingSchemePresenter

    @Inject
    lateinit var presenterProvider: Provider<BuildingSchemePresenter>

    @ProvidePresenter
    fun providePresenter(): BuildingSchemePresenter = presenterProvider.get().apply {
        val args = arguments!!
        @Suppress("UNCHECKED_CAST")
        places = (args.getParcelableArray(ARG_PLACES)!! as Array<Place>).toList()
    }

    private var imageHeight = 0
    private var imageWidth = 0

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
        with(Resources.getSystem().displayMetrics) {
            imageHeight = (heightPixels / 1.8f).roundToInt()
            imageWidth = widthPixels
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.apply {
            setOnTouchListener(pageIndicator)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    pageIndicator.onPageScrollStateChanged(state)
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    pageIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    pageIndicator.setSelected(position)
                    presenter.onPageChange(position)
                }
            })
        }

        pageIndicator.apply {
            setAnimationType(AnimationType.COLOR)
        }
    }

    override fun setPlaces(places: List<Place>, scrollPositions: SparseIntArray, page: Int) {
        viewPager.apply {
            adapter = PlacePagerAdapter(places, scrollPositions)
            setCurrentItem(page, false)
        }

        pageIndicator.apply {
            val pagesCount = places.size
            isVisible = pagesCount > 1
            count = pagesCount
        }
    }

    override fun showImage(url: String) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(ivScheme, ivScheme.transitionName)
        )
        findNavController().navigate(
                R.id.image_view_activity,
                ImageViewActivityArgs.Builder(url, null, null, ivScheme.transitionName).build().toBundle(),
                null,
                ActivityNavigatorExtras(options)
        )
    }

    override fun layout() = R.layout.fragment_building_scheme

    companion object {

        private const val ARG_PLACES = "places"

        fun newInstance(places: Array<Place>): BuildingSchemeFragment {
            return BuildingSchemeFragment().apply {
                arguments = bundleOf(ARG_PLACES to places)
            }
        }
    }

    private inner class PlacePagerAdapter(
            places: List<Place>,
            private val scrollPositions: SparseIntArray) : SimpleRecyclerViewAdapter<Place>(places) {
        override fun onBindItem(holder: ViewHolder, item: Place?, position: Int) {
            val place = item!!
            holder.apply {
                ivScheme.apply {
                    updateLayoutParams {
                        height = imageHeight
                        width = imageWidth
                    }
                    transitionName = place.image

                    Picasso.get()
                            .load(place.image)
                            .error(R.drawable.ic_broken_image)
                            .into(this)

                    setOnClickListener { presenter.onImageClick(place, position) }
                }

                val title = place.name
                tvDescriptionTitle.apply {
                    text = title
                    isVisible = !title.isNullOrEmpty()
                }

                val description = place.description
                tvDescription.apply {
                    text = description
                    isVisible = !description.isNullOrEmpty()
                }

                scrollContainer.apply {
                    val y = scrollPositions[position]
                    doOnNextLayout { scrollTo(0, y) }
                    setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                        presenter.onScrollPositionChange(scrollY, position)
                    })
                }
            }
        }

        override fun getItemLayout(itemView: Int) = R.layout.item_building_scheme
    }
}
