package com.example.ui.search.qr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.R
import com.example.data.models.Event
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_qr_scanner.*
import javax.inject.Inject
import javax.inject.Provider


class QrScannerFragment : BaseFragment(), QrScannerContract.View, ToolbarFragment {
    override val title: CharSequence
        get() = getString(R.string.qr_scan_label)

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
            decodeCallback = DecodeCallback { presenter.onDecodeQrCode(it.text) }
        }
        btnToEnterCode.setOnClickListener { presenter.onEnterCodeClick() }
        btnPermissionRequest.setOnClickListener { presenter.onRequestPermissionClick() }
    }

    override fun startPreview() {
        clScanner.isVisible = true
        clPermissionRequest.isVisible = false
        codeScanner.apply { if (!isPreviewActive) startPreview() }
    }

    override fun showNoPermission() {
        clScanner.isVisible = false
        clPermissionRequest.isVisible = true
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun showEvent(event: Event) {

    }

    override fun showEventNotFoundError() {
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.qr_scan_not_found_event)
                .setPositiveButton(R.string.ok) { _, _ -> codeScanner.startPreview() }
                .setOnCancelListener { codeScanner.startPreview() }
                .show()
    }

    override fun showEnterCode() {
        findNavController().navigate(QrScannerFragmentDirections.qrScannerToEnterCode())
    }

    override fun showAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun layout() = R.layout.fragment_qr_scanner
}