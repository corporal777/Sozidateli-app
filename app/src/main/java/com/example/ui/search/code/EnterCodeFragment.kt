package com.example.ui.search.code

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_enter_code.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class EnterCodeFragment : BaseFragment(), EnterCodeContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.code_input_label)

    @InjectPresenter
    lateinit var presenter: EnterCodePresenter

    @Inject
    lateinit var presenterProvider: Provider<EnterCodePresenter>

    @ProvidePresenter
    fun providePresenter(): EnterCodePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etCode.apply {
            setOnKeyListener { _, keyCode, _ ->
                when (keyCode) {
                    EditorInfo.IME_ACTION_DONE -> {
                        presenter.onSearchClick(etCode.text.toString())
                        true
                    }
                    else -> false
                }
            }
            requestFocus()

            onTextChanged {
                btnSearch.isEnabled = !it.isNullOrEmpty()
            }
        }
        btnSearch.apply {
            isEnabled = !etCode.text.isNullOrEmpty()
            setOnClickListener { presenter.onSearchClick(etCode.text.toString()) }
        }
    }

    override fun showEvent(event: Event) {
        findNavController().navigate(EnterCodeFragmentDirections.enterEventCodeFragmentToAboutEventFragment(event.id))
    }

    override fun showEventNotFoundError() {
        showToast(R.string.qr_scan_not_found_event)
    }

    override fun layout() = R.layout.fragment_enter_code
}
