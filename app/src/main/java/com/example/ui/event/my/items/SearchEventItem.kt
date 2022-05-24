package com.example.ui.event.my.items

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemSearchActivityBinding
import com.example.databinding.ItemSearchEventBinding
import com.example.util.SearchInput
import com.facebook.internal.Utility.isNullOrEmpty
import com.xwray.groupie.databinding.BindableItem
import com.yandex.metrica.impl.ob.it
import onTextChanged

class SearchEventItem(
    val onSearchTextChange: (search: String) -> Unit,
    val onSearchTextSubmit: (search: String) -> Unit,
    val onFilterClick: () -> Unit
) : BindableItem<ItemSearchEventBinding>() {


    override fun bind(viewBinding: ItemSearchEventBinding, position: Int) {
        viewBinding.apply {
            etSearch.apply {
                SearchInput(this).apply {
                    setOnTextChange {
                        onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        onSearchTextSubmit(it)
                    }
                }

                onTextChanged {
                    btnClear.isVisible = !it.isNullOrEmpty()
                }
                btnClear.apply {
                    btnClear.isVisible = !etSearch.text.isNullOrEmpty()
                    setOnClickListener { etSearch.text = null }
                }

                btnFilter.setOnClickListener {
                    onFilterClick.invoke()
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

    override fun getLayout(): Int = R.layout.item_search_event
}