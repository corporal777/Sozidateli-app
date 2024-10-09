package com.example.ui.base.bottomSheet

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import androidx.viewbinding.ViewBindings
import com.example.app.databinding.BottomSheetEventDetailInformationBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BaseBottomSheetDialog<B : ViewBinding>(context: Context) :
    BottomSheetDialog(context) {

    lateinit var mBinding: B

    init {
        init()

    }

    private fun init(){
        mBinding = getViewBinding().value



        setContent()
    }



    abstract fun setContent()
    abstract fun getViewBinding(): Lazy<B>
}