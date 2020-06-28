package com.example.ui.event.location.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.res.Resources
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MapInfo
import com.example.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt


class MapFragment private constructor() : BaseFragment(), MapContract.View, OnMapReadyCallback {

    @InjectPresenter
    lateinit var presenter: MapPresenter

    @Inject
    lateinit var presenterProvider: Provider<MapPresenter>

    @ProvidePresenter
    fun providePresenter(): MapPresenter = presenterProvider.get().apply {
        mapInfo = requireArguments().getParcelable(ARG_MAP_INFO)!!
    }

    private lateinit var googleMap: GoogleMap

    @SuppressLint("ClickableViewAccessibility")
    override fun initializeMap() {
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

        btnShare.setOnClickListener { presenter.onShareClick() }
        btnGoTo.setOnClickListener { presenter.onOpenRouteClick() }
    }

    override fun showContent() {
        scrollContainer.isVisible = true
    }

    @SuppressLint("MissingPermission")
    override fun enableCurrentLocation(enable: Boolean) {
        googleMap.isMyLocationEnabled = enable
    }

    override fun setMarker(lat: Double, lon: Double) {
        val latLng = LatLng(lat, lon)
        googleMap.addMarker(MarkerOptions().position(latLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }

    override fun setDescription(title: String?, description: String?) {
        tvDescriptionTitle.apply {
            text = title
            isVisible = !title.isNullOrEmpty()
        }

        tvDescription.apply {
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
            val shareIntent = Intent(ACTION_SEND)
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

    override fun layout() = R.layout.fragment_map

    companion object {
        private const val ARG_MAP_INFO = "mapInfo"

        fun newInstance(mapInfo: MapInfo): MapFragment {
            return MapFragment().apply {
                arguments = bundleOf(ARG_MAP_INFO to mapInfo)
            }
        }
    }
}
