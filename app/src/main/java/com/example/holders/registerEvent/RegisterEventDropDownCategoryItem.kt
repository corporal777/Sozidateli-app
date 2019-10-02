package com.example.holders.registerEvent

import com.example.R
import com.example.data.models.Category
import com.example.ui.request.RequestPresenter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.register_event_drop_down.view.*
import me.srodrigo.androidhintspinner.HintAdapter
import me.srodrigo.androidhintspinner.HintSpinner


class RegisterEventDropDownCategoryItem(private val categories: ArrayList<Category>, private val presenter: RequestPresenter, private val selectedCategory: Category?) : Item() {

    private var showArray: Array<String>? = null


    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {

            if (showArray == null) {
                showArray = categories.map { it.name }.toTypedArray()
                val hintSpinner = HintSpinner<String>(
                        spinner,
                        HintAdapter(context, R.string.register_event_select_category_hint, showArray?.toList()),
                        HintSpinner.Callback<String> { position, itemAtPosition ->
                            presenter.onDataChange("category_id", categories[position].id)
                        })
                hintSpinner.init()

                selectedCategory?.let { selectedCategory ->
                    categories.forEach {
                        if (selectedCategory.id == it.id) {
                            spinner.setSelection(categories.indexOf(it))
                            presenter.onDataChange("category_id", it.id)
                        }
                    }
                }

            }

        }
    }

    override fun getLayout() = R.layout.register_event_drop_down
}