package com.example.ui.event.activities

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSearchActivityBinding
import com.example.util.SearchInput
import com.google.firebase.crashlytics.internal.common.CommonUtils.hideKeyboard
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.fragment_search_tabs.*
import onTextChanged

class SearchActivityItem(
    val onSearchTextChange: (search : String) -> Unit,
    val onSearchTextSubmit: (search : String) -> Unit
) : BindableItem<ItemSearchActivityBinding>() {


    override fun bind(viewBinding: ItemSearchActivityBinding, position: Int) {
        viewBinding.apply {
            etSearch.apply {
                SearchInput(this).apply {
                    setOnTextChange {
                        onSearchTextChange(it)
                        //presenter.onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        onSearchTextSubmit(it)
                        //presenter.onSearchTextSubmit(it)
                    }
                }

                onTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                }
                btnClear.apply {
                    btnClear.isVisible = !etSearch.text.isNullOrEmpty()
                    setOnClickListener { etSearch.text = null }
                }

                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_search_field_rounded_focused
                        else R.drawable.background_search_field_rounded_normal
                    )
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_search_activity
}