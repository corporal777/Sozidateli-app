package com.example.ui.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.example.ui.views.dialogs.CustomProgressDialog
import dagger.android.AndroidInjection

abstract class BaseCustomActivity<B : ViewBinding> : MvpAppCompatActivity() {

    lateinit var mBinding: B

    lateinit var mProgressDialog : CustomProgressDialog

    var isBackEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)
        postponeEnterTransition()

        mBinding = getViewBinding()
        setContentView(mBinding.root)

        mProgressDialog = CustomProgressDialog(this)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    abstract fun getViewBinding(): B

}