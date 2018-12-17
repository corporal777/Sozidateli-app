package com.example.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.PermissionChecker
import androidx.navigation.fragment.findNavController
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.R
import com.example.data.models.Event
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_qr_scanner.*
import javax.inject.Inject
import javax.inject.Provider

class QrScannerFragment : BaseFragment(), QrScannerContract.View {

    @InjectPresenter
    lateinit var presenter: QrScannerPresenter

    @Inject
    lateinit var presenterProvider: Provider<QrScannerPresenter>

    @ProvidePresenter
    fun providePresenter(): QrScannerPresenter = presenterProvider.get()

    private lateinit var codeScanner: CodeScanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner = CodeScanner(requireActivity(), scannerView).apply {
            decodeCallback = DecodeCallback {
                activity?.runOnUiThread { presenter.onDecodeQrCode(it.text) }
            }
        }
    }

    override fun checkCameraPermission(grantedResult: (Boolean) -> Unit) {
        grantedResult.invoke(checkCameraPermission())
    }

    override fun requestCameraPermission() = requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_CAMERA) {
            presenter.onCameraPermissionGranted()
        }
    }

    override fun startPreview() {
        codeScanner.apply { if (!isPreviewActive) startPreview() }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun checkCameraPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun showEvent(event: Event) {
        findNavController().navigate(R.id.about_event_navigation, bundleOf("event" to event))
    }

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_qr_scanner

    companion object {

        private const val PERMISSION_CAMERA = 1
    }
}