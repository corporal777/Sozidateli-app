package com.example.ui.event.location.buildingScheme

import android.util.SparseIntArray
import androidx.core.util.set
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Place
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class BuildingSchemePresenter
@Inject constructor(appData: AppData) : BasePresenter<BuildingSchemeContract.View>(appData), BuildingSchemeContract.Presenter {

    lateinit var places: List<Place>

    private val placeScrollPosition = SparseIntArray()

    private var pagePosition: Int = 0

    override fun attachView(view: BuildingSchemeContract.View?) {
        super.attachView(view)
        viewState.setPlaces(places.filter { !it.image.isNullOrEmpty() }, placeScrollPosition, pagePosition)
    }

    override fun onImageClick(place: Place, position: Int) {
        viewState.showImage(place.image!!)
    }

    override fun onScrollPositionChange(scroll: Int, position: Int) {
        placeScrollPosition[position] = scroll
    }

    override fun onPageChange(position: Int) {
        pagePosition = position
    }
}
