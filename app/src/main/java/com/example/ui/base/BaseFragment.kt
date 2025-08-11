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
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.examle.data.models.UserDetail
import com.example.app.R
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.StateType
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

abstract class BaseFragment<binding : ViewDataBinding> : MvpAppCompatFragment(), BaseContract.View {

    lateinit var mBinding: binding

    private val params = PermissionsParams()
    private var mActivity: BaseActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) this.mActivity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (animationType() == AnimType.AXIS) {
            postponeEnterTransition()
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = (350).toLong()
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
                duration = (450).toLong()
            }
        } else if (animationType() == AnimType.FADE) {
            enterTransition = MaterialFadeThrough()
            exitTransition = MaterialFadeThrough()
        } else return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::mBinding.isInitialized.not()) {
            mBinding = DataBindingUtil.inflate(inflater, layout(), container, false)
            mBinding.lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }


    @LayoutRes
    abstract fun layout(): Int

    override fun showSnackBar(@StringRes message: Int) = showSnackBar(getString(message))
    override fun showSnackBar(message: String) { mActivity?.showSnackBar(message) }

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    override fun showProgressBarLoading() { mActivity?.showProgressBarLoading() }
    override fun hideProgressBarLoading() { mActivity?.hideProgressBarLoading() }

    override fun showProgressBarDialogLoading() { mActivity?.showProgressBarDialogLoading() }
    override fun hideProgressBarDialogLoading() { mActivity?.hideProgressBarDialogLoading() }

    override fun hideAllLoadingDialogs() { mActivity?.hideAllLoadingDialogs() }

    override fun showCustomLoading() {}
    override fun hideCustomLoading() {}



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

    override fun setIgnoreTokenListener(isIgnore: Boolean) {
        mActivity?.setIgnoreTokenListener(isIgnore)
    }

    override fun showEventRegistrationSuccessDialog() {
        mActivity?.showEventRegistrationSuccessDialog()
    }

    override fun navigateUp() {
        mActivity?.navigateUp()
    }

    override fun showNoConnectionMessage(show: Boolean) {
        mActivity?.showNoConnectionMessage(show)
    }

    override fun showRequestErrorMessage() {
        mActivity?.showRequestErrorMessage()
    }

    override fun showEmailErrorMessage() {
        mActivity?.showEmailErrorMessage()

    }

    override fun showPhoneErrorMessage() {
        mActivity?.showPhoneErrorMessage()
    }

    override fun showErrorMessage(canGoBack: Boolean, message: String) {
        DefaultAlertDialog(requireContext(), null, message)
            .setSelectCallback { if (canGoBack) findNavController().navigateUp() }
    }

    override fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?) {

    }

    override fun showAddedToFavoriteDialog() {
        mActivity?.showAddedToFavoriteDialog()
    }

    override fun showRemovedFromFavoriteDialog() {
        mActivity?.showRemovedFromFavoriteDialog()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        hideAllLoadingDialogs()
        mBinding.unbind()
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        try {
            if (isAdded && context != null) operation(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class PermissionsParams {
        var permissionsToRequest = arrayOf<String>()
        var permissionsGrantedCallback: () -> Unit = {}
        var permissionsNotGrantedCallback: () -> Unit = {}
        var requestCode: Int = -1
    }

    val askMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
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
            DefaultAlertDialog(
                requireActivity(),
                null,
                getString(R.string.warning_dialog_text),
                getString(R.string.yes),
                getString(R.string.no)
            ).setSelectCallback { data.invoke() }
        } else data.invoke()
    }


    fun updateAppBarViews(offset: Float) {
        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED -> onExpandedState()
                        TO_COLLAPSED -> onCollapsedState()
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }

                else -> cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
            }
        }
    }

    open fun onExpandedState() {}
    open fun onCollapsedState() {}
    open fun scrollToFirstItem() {}

    open fun animationType(): AnimType {
        return AnimType.NONE
    }

    enum class AnimType {
        FADE, AXIS, NONE
    }


    private var cashCollapseState: Pair<Int, Int>? = null

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }

}

