package com.example.ui.event.location.map.redesign

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MapInfo
import com.example.ui.base.BaseFragment
import com.example.ui.event.location.map.MapFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map_new.*
import kotlinx.android.synthetic.main.fragment_map_new.btnGoTo
import kotlinx.android.synthetic.main.fragment_map_new.btnShare
import kotlinx.android.synthetic.main.fragment_map_new.flMapContainer
import kotlinx.android.synthetic.main.fragment_map_new.flMapTouchWrapper
import kotlinx.android.synthetic.main.fragment_map_new.llMapAction
import kotlinx.android.synthetic.main.fragment_map_new.scrollContainer
import kotlinx.android.synthetic.main.fragment_map_new.tvDescription
import kotlinx.android.synthetic.main.fragment_map_new.tvDescriptionTitle
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class MapFragmentNew : BaseFragment(), MapContractNew.View , OnMapReadyCallback{

    @InjectPresenter
    lateinit var presenter: MapPresenterNew

    @Inject
    lateinit var presenterProvider: Provider<MapPresenterNew>


    @ProvidePresenter
    fun providePresenter(): MapPresenterNew = presenterProvider.get().apply {
        val args =
            MapFragmentNewArgs.fromBundle(requireArguments())
        mapInfo = args.mapInfo
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mMapView: MapView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener {
            findNavController().popBackStack()
        }
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
                getMapAsync(this@MapFragmentNew)
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

    override fun layout() = R.layout.fragment_map_new

    companion object {
        private const val ARG_MAP_INFO = "mapInfo"

    }

}