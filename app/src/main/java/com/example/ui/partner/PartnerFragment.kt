package com.example.ui.partner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Partner
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_about_forum.*
import javax.inject.Inject
import javax.inject.Provider

class PartnerFragment : BaseFragment(), PartnerContract.View, ToolbarFragment {
    override val title: String
        get() = getString(R.string.partners_label)

    @InjectPresenter
    lateinit var presenter: PartnerPresenter

    @Inject
    lateinit var presenterProvider: Provider<PartnerPresenter>

    @ProvidePresenter
    fun providePresenter(): PartnerPresenter = presenterProvider.get()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            val data = PartnerFragmentArgs.fromBundle(it)
            presenter.partner = data.partner
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setData(partner: Partner) {
        Picasso.get().load(partner.logo.let { if (it.isNullOrEmpty()) null else it }).placeholder(R.mipmap.ic_launcher_background).into(ivLogo)
        tvInfo.text = partner.description
        partner.name?.let {
            setLabel(it)
        }
    }

    fun setLabel(label: String) {
        (activity as AppCompatActivity?)?.supportActionBar?.title = label
    }

    override fun layout() = R.layout.fragment_partner
}
