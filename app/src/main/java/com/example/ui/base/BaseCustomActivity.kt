package com.example.ui.base

import android.graphics.Bitmap
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.databinding.ActivityImageCropBinding
import com.example.ui.gallery.cropImage.CropImageActivityArgs
import com.example.ui.gallery.cropImage.CropImagePresenter
import com.example.ui.gallery.cropImage.cropHelper.CropImageView
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import com.example.ui.views.dialogs_new.CustomProgressDialog
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Provider

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