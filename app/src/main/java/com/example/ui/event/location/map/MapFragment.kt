package com.example.ui.event.location.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import com.example.app.R
import com.example.data.models.MapInfo
import com.example.app.databinding.FragmentMapBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class MapFragment(
    private val info: MapInfo
) : BaseBottomSheetFragment<FragmentMapBinding>(), MapContract.View, OnMapReadyCallback {

    @InjectPresenter(tag = MAP_FRAGMENT_TAG)
    lateinit var presenter: MapPresenter

    @Inject
    lateinit var presenterProvider: Provider<MapPresenter>


    @ProvidePresenter(tag = MAP_FRAGMENT_TAG)
    fun providePresenter(): MapPresenter = presenterProvider.get().apply {
        mapInfo = info
    }

    private lateinit var googleMap: GoogleMap


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.ivBack.setOnClickListener { dismiss() }
        mBinding.btnShare.setOnClickListener { presenter.onShareClick() }
        mBinding.btnGoTo.setOnClickListener { presenter.onOpenRouteClick() }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initializeMap() {
        mBinding.apply {
            flMapContainer.apply {
                updateLayoutParams {
                    val dh = Resources.getSystem().displayMetrics.heightPixels / 1.8f
                    height = dh.roundToInt()
                }

                (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).apply {
                    getMapAsync(this@MapFragment)
                }
                isVisible = true
            }

            llMapAction.isVisible = true

            flMapTouchWrapper.apply {
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_UP -> {
                            scrollContainer.requestDisallowInterceptTouchEvent(false)
                            true
                        }

                        MotionEvent.ACTION_DOWN,
                        MotionEvent.ACTION_MOVE -> {
                            scrollContainer.requestDisallowInterceptTouchEvent(true)
                            false
                        }

                        else -> true
                    }
                }
            }
        }

    }

    override fun showContent() {
        mBinding.scrollContainer.isVisible = true
    }

    override fun setMarker(lat: Double, lon: Double) {
        val latLng = LatLng(lat, lon)
        googleMap.addMarker(MarkerOptions().position(latLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }

    override fun setDescription(title: String?, description: String?) {
        mBinding.tvDescriptionTitle.apply {
            text = title
            isVisible = !title.isNullOrEmpty()
        }

        mBinding.tvDescription.apply {
            text = description
            isVisible = !description.isNullOrEmpty()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        presenter.onMapReady()
    }

    override fun shareUrl(url: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.map_sharing)))
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.map_sharing_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun openUrl(url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.map_route_error, Toast.LENGTH_LONG).show()
        }
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, MAP_FRAGMENT_TAG)

    override fun layout() = R.layout.fragment_map

    companion object {
        const val MAP_FRAGMENT_TAG = "map_fragment_tag"
    }
}