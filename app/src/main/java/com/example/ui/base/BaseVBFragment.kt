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
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.ScaleProvider
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
        super.onCreate(savedInstanceState)
        if (animationType() == AnimType.AXIS) {
            postponeEnterTransition()
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = (300).toLong()
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
                duration = (400).toLong()
            }
        } else if (animationType() == AnimType.FADE) {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                primaryAnimatorProvider.apply { if (this is ScaleProvider) incomingStartScale = 0.94f }
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBarViewsState()
    }

    @LayoutRes
    abstract fun layout(): Int

    override fun showSnackBar(@StringRes message: Int) = showSnackBar(getString(message))
    override fun showSnackBar(message: String) { mActivity?.showSnackBar(message) }

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) { mActivity?.showToast(message) }


    override fun showProgressBarLoading() { mActivity?.showProgressBarLoading() }
    override fun hideProgressBarLoading() { mActivity?.hideProgressBarLoading() }

    override fun showProgressBarDialogLoading() { mActivity?.showProgressBarDialogLoading() }
    override fun hideProgressBarDialogLoading() { mActivity?.hideProgressBarDialogLoading() }

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

    override fun showBrowser(url: String) {
        mActivity?.showBrowser(url)
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        hideAllLoadingDialogs()
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


    open fun onExpandedState(withAnim : Boolean) {}
    open fun onCollapsedState(withAnim : Boolean) {}
    open fun scrollToFirstItem() {}
    open fun animationType(): AnimType = AnimType.NONE

    var isEmptyData = false
    open fun setEmptyDataPlaceholder(show: Boolean){
        isEmptyData = show
    }

    enum class AnimType {
        FADE, AXIS, NONE
    }

    fun isPreviousDestination(id: Int): Boolean {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val prevId = navHostFragment.navController.previousBackStackEntry?.destination?.id
        return prevId == id
    }

    private fun setAppBarViewsState(){
        when (collapseState?.first) {
            TO_EXPANDED -> onExpandedState(false)
            TO_COLLAPSED -> onCollapsedState(false)
        }
    }
    fun updateAppBarViews(offset: Float) {
        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, collapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, collapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                collapseState != null && collapseState != this -> {
                    when (first) {
                        TO_EXPANDED -> onExpandedState(true)
                        TO_COLLAPSED -> onCollapsedState(true)
                    }
                    collapseState = Pair(first, SWITCHED)
                }

                else -> collapseState = Pair(first, WAIT_FOR_SWITCH)
            }
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