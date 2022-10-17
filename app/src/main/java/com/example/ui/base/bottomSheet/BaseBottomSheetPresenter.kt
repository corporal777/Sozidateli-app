package com.example.ui.base.bottomSheet

import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.ui.base.BasePresenter
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

abstract class BaseBottomSheetPresenter<V : BaseBottomSheetContract.View>(
    private val appData: AppData
) : BasePresenter<V>(appData), BaseBottomSheetContract.Presenter {


}