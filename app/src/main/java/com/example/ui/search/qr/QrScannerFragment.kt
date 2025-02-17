package com.example.ui.search.qr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.app.R
import com.example.app.databinding.FragmentQrScannerBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.views.toolbar.ToolbarContent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class QrScannerFragment : BaseVBFragment<FragmentQrScannerBinding>(), QrScannerContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: QrScannerPresenter

    @Inject
    lateinit var presenterProvider: Provider<QrScannerPresenter>

    @ProvidePresenter
    fun providePresenter(): QrScannerPresenter = presenterProvider.get()

    private var codeScanner: CodeScanner? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (codeScanner == null){
            codeScanner = CodeScanner(requireActivity(), mBinding.scannerView).apply {
                decodeCallback = DecodeCallback { presenter.onDecodeQrCode(it.text) }
            }
        }
        mBinding.apply {
            btnToEnterCode.setOnClickListener { presenter.onEnterCodeClick() }
            btnPermissionRequest.setOnClickListener { presenter.onRequestPermissionClick() }
        }
    }

    override fun startPreview() {
        mBinding.clScanner.isVisible = true
        mBinding.clPermissionRequest.isVisible = false
        codeScanner?.apply { if (!isPreviewActive) startPreview() }
    }

    override fun showNoPermission() {
        mBinding.clScanner.isVisible = false
        mBinding.clPermissionRequest.isVisible = true
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    override fun showEvent(eventId: String) {
        findNavController().navigate(QrScannerFragmentDirections.qrScannerToAboutEventFragmentNew(eventId))
    }

    override fun showEventNotFoundError() {
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.qr_scan_not_found_event)
                .setPositiveButton(R.string.ok) { _, _ -> codeScanner?.startPreview() }
                .setOnCancelListener { codeScanner?.startPreview() }
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

    override fun binding() = FragmentQrScannerBinding::class.java
    override fun layout() = R.layout.fragment_qr_scanner
    override val title: CharSequence by lazy { getString(R.string.qr_scan_label) }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {  }
}