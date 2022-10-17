package com.example.ui.search.user

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.data.models.UserDetail
import com.xwray.groupie.Group
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment : AbstractSearchUserFragment<SearchUserPresenter>() {

    @InjectPresenter
    override lateinit var presenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}