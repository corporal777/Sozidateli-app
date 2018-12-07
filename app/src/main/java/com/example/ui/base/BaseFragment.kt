package com.example.ui.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : MvpAppCompatFragment(), BaseContract.View {

    private var mActivity: BaseActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.mActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity?.apply { if (isShowToolbar()) showToolbar() else hideToolbar() }
        return inflater.inflate(layout(), container, false)
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    @LayoutRes
    abstract fun layout(): Int

    protected open fun isShowToolbar(): Boolean = false

    override fun showToast(@StringRes message: Int) = showToast(getString(message))

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateUp() {
        mActivity?.navigateUp()
    }
}