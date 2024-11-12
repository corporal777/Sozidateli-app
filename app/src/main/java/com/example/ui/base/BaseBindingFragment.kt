package com.example.ui.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.ViewBindingProperty
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.FragmentSearchTabsBinding
import com.example.data.models.UserDetail
import com.example.ui.state.UserState
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.views.dialogs.ChangeStateBottomDialog
import com.example.ui.views.dialogs.ClickType
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.StateType
import com.example.util.Utils
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.android.support.AndroidSupportInjection
import kotlin.reflect.KClass

abstract class BaseBindingFragment(res: Int) : MvpAppCompatFragment(res), BaseContract.View {

    //abstract fun layout(): Int

    private var mActivity: BaseActivity? = null

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

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    override fun showToast(@StringRes message: Int) = showToast(getString(message))
    override fun showToast(message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

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

    override fun showCustomProgressDialog() {
        mActivity?.showCustomProgressDialog()
    }

    override fun hideCustomProgressDialog() {
        mActivity?.hideCustomProgressDialog()
    }

    override fun hideAllLoadingDialogs() {
        mActivity?.hideAllLoadingDialogs()
    }

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
        ChangeStateBottomDialog(requireActivity(), type)
            .setClickCallback {
                when (it) {
                    ClickType.INFO -> findNavController().navigate(R.id.userStateFragment)
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

                                    else -> {}
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
            }.show()
    }

    override fun showAddedToFavoriteDialog() {
        mActivity?.showAddedToFavoriteDialog()
    }

    override fun showRemovedFromFavoriteDialog() {
        mActivity?.showRemovedFromFavoriteDialog()
    }


    override fun onDestroyView() {
        hideKeyboard()
        hideAllLoadingDialogs()
        hideCustomProgressDialog()
        super.onDestroyView()
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

    fun isPreviousDestination(id: Int): Boolean {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val prevId = navHostFragment.navController.previousBackStackEntry?.destination?.id
        return prevId == id
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