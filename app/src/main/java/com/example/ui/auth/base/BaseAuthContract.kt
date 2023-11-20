package com.example.ui.auth.base

import com.example.ui.base.BaseContract

interface BaseAuthContract {
    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter {
        fun authVk()
    }
}
