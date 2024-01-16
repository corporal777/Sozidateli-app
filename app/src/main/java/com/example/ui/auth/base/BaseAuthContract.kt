package com.example.ui.auth.base

import android.content.Context
import com.example.ui.base.BaseContract

interface BaseAuthContract {
    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter {
    }
}
