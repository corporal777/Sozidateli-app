package com.example.ui.qrscanner

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.budiyev.android.codescanner.*
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.util.REQUEST_CAMERA
import com.example.util.REQUIRED_CAMERA_PERMISSIONS
import kotlinx.android.synthetic.main.fragment_auth_web.*
import javax.inject.Inject
import javax.inject.Provider

class QrScannerToAuthWebFragment : BaseFragment(), QrScannerToAuthWebContract.View {

    private lateinit var codeScanner: CodeScanner


    @InjectPresenter
    lateinit var mPresenter: QrScannerToAuthWebPresenter

    @Inject
    lateinit var presenterProvider: Provider<QrScannerToAuthWebPresenter>

    @ProvidePresenter
    fun providePresenter(): QrScannerToAuthWebPresenter = presenterProvider.get().apply {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner = CodeScanner(requireActivity(), scannerView)
        setupScanner()

        ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        tvAuthWebSite.setOnClickListener {

        }

    }


    private fun setupScanner() {
        PermissionsBuilder(REQUEST_CAMERA)
            .addPermissions(REQUIRED_CAMERA_PERMISSIONS)
            .setPermissionsGrantedCallback {
                codeScanner()
            }.setPermissionsNotGrantedCallback {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    REQUIRED_CAMERA_PERMISSIONS,
                    REQUEST_CAMERA
                )
            }
            .request()
    }

    private fun codeScanner() {
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            autoFocusMode = AutoFocusMode.SAFE
            isAutoFocusEnabled = true
            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {
                    Log.e("Result", "Camera Result ${it.text}")
                    val mDecodedCode = it.text
//                    showToast(mDecodedCode)
                    mPresenter.onSuccessScanning(mDecodedCode)
                    stopPreview()
                }
            }
            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    mPresenter.onErrorScanning()
                    Log.e("Result", "Camera error ${it.message}")
                }
            }
            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
            codeScanner.startPreview()
        }
    }

    override fun layout(): Int = R.layout.fragment_auth_web

    override fun goToAuthWebsite(code: String) {
        findNavController().navigate(
            QrScannerToAuthWebFragmentDirections.actionQrScannerFragmentToAuthWebsiteFragment(
                code
            )
        )
    }

    override fun showErrorScanningMessage() {
        val message = "Не удалось отсканировать"
        MessageDialogWithBrownButton(requireContext(), message).setSelectCallback {  }
    }
}