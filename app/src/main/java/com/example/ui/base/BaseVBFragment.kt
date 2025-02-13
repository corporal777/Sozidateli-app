package com.example.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.StateType
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.android.support.AndroidSupportInjection
import dev.androidbroadcast.vbpd.CreateMethod
import dev.androidbroadcast.vbpd.viewBinding

abstract class BaseVBFragment<VB : ViewBinding> : MvpAppCompatFragment(), BaseContract.View {

    private var mActivity: BaseActivity? = null

    abstract fun binding() : Class<VB>
    private val localBinding get() = binding()
    protected val mBinding: VB by viewBinding(localBinding)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) this.mActivity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
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
        return inflater.inflate(layout(), container, false)
    }


    @LayoutRes
    abstract fun layout(): Int

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) { mActivity?.showToast(message) }

    override fun showLoadingDialog() { mActivity?.showLoadingDialog() }
    override fun hideLoadingDialog() { mActivity?.hideLoadingDialog() }

    override fun showProgressBarLoadingDialog() { mActivity?.showProgressBarLoadingDialog() }
    override fun hideProgressBarLoadingDialog() { mActivity?.hideProgressBarLoadingDialog() }

    override fun showCustomProgressDialog() { mActivity?.showCustomProgressDialog() }
    override fun hideCustomProgressDialog() { mActivity?.hideCustomProgressDialog() }

    override fun hideAllLoadingDialogs() { mActivity?.hideAllLoadingDialogs() }

    override fun showCustomLoading() {}
    override fun hideCustomLoading() {}

    override fun hideKeyboard() { mActivity?.hideKeyboard() }
    override fun hideKeyboard(v: View?) { mActivity?.hideKeyboard(v) }

    override fun showKeyboard() { mActivity?.showKeyboard() }
    fun showKeyboard(view: View) { mActivity?.showKeyboard(view) }

    override fun setIgnoreTokenListener(isIgnore: Boolean) {
        mActivity?.setIgnoreTokenListener(isIgnore)
    }

    override fun showEventRegistrationSuccessDialog() {
        mActivity?.showEventRegistrationSuccessDialog()
    }

    override fun navigateUp() {
        mActivity?.navigateUp()
    }

    override fun showNoConnectionMessage(show: Boolean) { mActivity?.showNoConnectionMessage(show) }

    override fun showRequestErrorMessage() { mActivity?.showRequestErrorMessage() }
    override fun showEmailErrorMessage() { mActivity?.showEmailErrorMessage() }
    override fun showPhoneErrorMessage() { mActivity?.showPhoneErrorMessage() }

    override fun showErrorMessage(canGoBack: Boolean, message: String) {
        DefaultAlertDialog(requireContext(), null, message)
            .setSelectCallback { if (canGoBack) findNavController().navigateUp() }
    }

    override fun showStateErrorMessage(type: StateType, hasBase: Boolean, user: UserDetail?) {
        mActivity?.showStateErrorMessage(type, hasBase, user)
    }

    override fun showAddedToFavoriteDialog() {
        mActivity?.showAddedToFavoriteDialog()
    }

    override fun showRemovedFromFavoriteDialog() {
        mActivity?.showRemovedFromFavoriteDialog()
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        hideAllLoadingDialogs()
        hideCustomProgressDialog()
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        try {
            if (isAdded && context != null) operation(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    open fun onExpandedState() {}
    open fun onCollapsedState() {}
    open fun scrollToFirstItem() {}

    open fun animationType(): AnimType = AnimType.NONE

    enum class AnimType {
        FADE, AXIS, NONE
    }

    fun isPreviousDestination(id: Int): Boolean {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val prevId = navHostFragment.navController.previousBackStackEntry?.destination?.id
        return prevId == id
    }

    open fun setAppBarViewsState(state : Int, isRestore : Boolean){}
    fun updateAppBarViews(offset: Float?) {
        if (offset == null) setAppBarViewsState(collapseState?.first ?: 0, true)
        else when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, collapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, collapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            if (collapseState != null && collapseState != this){
                setAppBarViewsState(first, false)
                collapseState = Pair(first, SWITCHED)
            } else collapseState = Pair(first, WAIT_FOR_SWITCH)
        }
    }

    private var collapseState: Pair<Int, Int>? = null
    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }

}