package com.example.ui.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.UserDetail
import com.example.ui.state.UserState
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.views.*
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.example.util.Utils
import com.google.android.material.transition.MaterialSharedAxis
import dagger.android.support.AndroidSupportInjection
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

abstract class BaseFragment : MvpAppCompatFragment(), BaseContract.View {

    private val params = PermissionsParams()
    protected var mActivity: BaseActivity? = null
        private set


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.mActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                e.printStackTrace()
            } else {
                Thread.currentThread().also { thread ->
                    thread.uncaughtExceptionHandler.uncaughtException(thread, e)
                }
            }
        }

        return inflater.inflate(layout(), container, false)
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    override fun showEnterAnimation() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = (250).toLong()
        }

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = (450).toLong()
        }
    }

    @LayoutRes
    abstract fun layout(): Int

    override fun showToast(@StringRes message: Int) = showToast(getString(message))

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadingDialog() {
        mActivity?.showLoadingDialog()
    }

    override fun hideLoadingDialog() {
        mActivity?.hideLoadingDialog()
    }

    override fun showProgressBarLoadingDialog() {
        mActivity?.showProgressBarLoadingDialog()
    }

    override fun hideProgressBarLoadingDialog() {
        mActivity?.hideProgressBarLoadingDialog()
    }

    override fun hideAllLoadingDialogs() {
        mActivity?.hideAllLoadingDialogs()
    }

    override fun showCustomProgressDialog() {
        mActivity?.showCustomProgressDialog()
    }

    override fun hideCustomProgressDialog() {
        mActivity?.hideCustomProgressDialog()
    }

    override fun hideKeyboard() {
        mActivity?.hideKeyboard()
    }

    override fun hideKeyboard(v: View?) {
        mActivity?.hideKeyboard(v)
    }

    override fun showKeyboard() {
        mActivity?.showKeyboard()
    }

    fun showKeyboard(view: View) {
        mActivity?.showKeyboard(view)
    }

    override fun navigateUp() {
        mActivity?.navigateUp()
    }

    override fun setIgnoreTokenListener(isIgnore: Boolean) {
        mActivity?.setIgnoreTokenListener(isIgnore)
    }

    override fun showNoConnectionMessage(show: Boolean) {
        mActivity?.showNoConnectionMessage(show)
    }

    override fun showRequestErrorMessage() {
        mActivity?.showRequestErrorMessage()
    }

    override fun showEmailErrorMessage() {
        context?.let {
            ApiErrorDialog(
                it, getString(R.string.email_exist_error_title),
                getString(R.string.email_exist_error_text)
            )
                .setSelectCallback { }
        }
    }

    override fun showErrorMessage(canGoBack: Boolean, message: String) {
        MessageDialogWithGreenButton(requireContext(), message).setSelectCallback {
            if (canGoBack) {
                findNavController().navigateUp()
            }
        }
    }

    override fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?) {
        ChangeStateDialog(requireActivity(), type)
            .setClickCallback {
                when (it) {
                    ClickType.INFO -> {
                        findNavController().navigate(R.id.userStateFragment)
                    }
                    ClickType.BASE -> {
                        findNavController().navigate(
                            R.id.mainInfoFragment,
                            bundleOf("type" to UserState.BASE, "screen" to 3)
                        )
                    }
                    ClickType.MAX -> {
                        if (hasBase) {
                            if (user != null) {
                                when (Utils.maxStateScreen(user)) {
                                    MaxStateScreenType.BASE ->
                                        findNavController().navigate(
                                            R.id.maxStatusContactsFragment,
                                            bundleOf("screen" to 1)
                                        )
                                    MaxStateScreenType.INTERESTS ->
                                        findNavController().navigate(
                                            R.id.maxStatusInterestsFragment,
                                            bundleOf("screen" to 1)
                                        )
                                    MaxStateScreenType.EDUCATION ->
                                        findNavController().navigate(
                                            R.id.maxStatusEducationFragment,
                                            bundleOf("screen" to 1)
                                        )
                                    MaxStateScreenType.WORK ->
                                        findNavController().navigate(
                                            R.id.maxStatusWorkFragment,
                                            bundleOf("screen" to 1)
                                        )
                                }
                            }
                        } else {
                            findNavController().navigate(
                                R.id.mainInfoFragment,
                                bundleOf("type" to UserState.MAX, "screen" to 1)
                            )
                        }
                    }
                }
            }
    }


    override fun showPhoneErrorMessage() {
        context?.let {
            ApiErrorDialog(
                it, getString(R.string.phone_exist_error_title),
                getString(R.string.phone_exist_error_text)
            )
                .setSelectCallback { }
        }
    }

    override fun showNotificationErrorMessage() {
        context?.let { FillProfileDialog(it).setSelectCallback { findNavController().navigate(R.id.user_profile_fragment) } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        hideAllLoadingDialogs()
        hideCustomProgressDialog()
    }

    class PermissionsParams {
        var permissionsToRequest = arrayOf<String>()
        var permissionsGrantedCallback: () -> Unit = {}
        var permissionsNotGrantedCallback: () -> Unit = {}
        var requestCode: Int = -1
    }

    val askMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (allPermissionsGranted(params.permissionsToRequest))
                params.permissionsGrantedCallback.invoke()
        }

    inner class PermissionsBuilder(requestCode: Int) {

        init {
            params.requestCode = requestCode
        }

        fun setPermissionsGrantedCallback(block: () -> Unit): PermissionsBuilder {
            params.permissionsGrantedCallback = block
            return this
        }

        fun setPermissionsNotGrantedCallback(block: () -> Unit): PermissionsBuilder {
            params.permissionsNotGrantedCallback = block
            return this
        }

        fun addPermissions(permission: Array<String>): PermissionsBuilder {
            params.permissionsToRequest = permission
            return this
        }

        fun request() {
            if (allPermissionsGranted(params.permissionsToRequest))
                params.permissionsGrantedCallback.invoke()
            else
                askMultiplePermissions.launch(params.permissionsToRequest)
        }
    }

    private fun allPermissionsGranted(permissionsToRequest: Array<String>) =
        permissionsToRequest.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    protected fun <T> showEditWarning(
        isBase: Boolean,
        isMax: Boolean,
        isEmptyBaseFields: Boolean,
        isEmptyMaxFields: Boolean,
        data: () -> T?
    ) {
        if ((isBase && isEmptyBaseFields) || (isMax && isEmptyMaxFields)) {
            WarningDialog(requireActivity(), resources.getString(R.string.warning_dialog_text))
                .setSelectCallback {
                    if (it) data.invoke()
                }
        } else data.invoke()
    }
}