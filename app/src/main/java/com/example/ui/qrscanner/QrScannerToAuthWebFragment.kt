package com.example.ui.qrscanner

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.example.R
import com.example.databinding.FragmentAuthWebBinding
import com.example.ui.base.BaseFragment
import com.example.ui.qrscanner.auth.AuthWebsiteFragmentArgs
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class QrScannerToAuthWebFragment : BaseFragment<FragmentAuthWebBinding>(),
    QrScannerToAuthWebContract.View {

    private var codeScanner: CodeScanner? = null

    @InjectPresenter
    lateinit var mPresenter: QrScannerToAuthWebPresenter

    @Inject
    lateinit var presenterProvider: Provider<QrScannerToAuthWebPresenter>

    @ProvidePresenter
    fun providePresenter(): QrScannerToAuthWebPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (codeScanner == null) {
            codeScanner = CodeScanner(requireActivity(), mBinding.scannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = CodeScanner.ALL_FORMATS
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.CONTINUOUS
                autoFocusMode = AutoFocusMode.SAFE
                isAutoFocusEnabled = true
                decodeCallback = DecodeCallback {
                    requireActivity().runOnUiThread {
                        mPresenter.onSuccessScanning(it.text)
                        stopPreview()
                    }
                }
                errorCallback = ErrorCallback {
                    requireActivity().runOnUiThread {
                        mPresenter.onErrorScanning()
                        stopPreview()
                    }
                }
            }
        }

        mBinding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun startPreview() {
        codeScanner?.apply { if (!isPreviewActive) startPreview() }
    }

    override fun showAuthWebsite(code: String, socketId: String) {
        findNavController().navigate(
            R.id.authWebsiteFragment,
            AuthWebsiteFragmentArgs.Builder(code, socketId).build().toBundle()
        )
    }

    override fun showErrorScanningMessage() {
        val message = "Не удалось отсканировать"
        MessageDialogWithBrownButton(requireContext(), message, isCancelable = false)
            .setSelectCallback { startPreview() }
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    override fun layout(): Int = R.layout.fragment_auth_web
}