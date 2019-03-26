package com.example.holders.registerEvent

import android.app.ActionBar
import android.app.DatePickerDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.R
import com.example.data.models.Category
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.ui.request.RequestPresenter
import com.example.util.Utils
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_drop_down.view.*
import java.util.*
import kotlin.collections.ArrayList
import me.srodrigo.androidhintspinner.HintSpinner
import me.srodrigo.androidhintspinner.HintAdapter




class RegisterEventDropDownCategoryItem(private val categories: ArrayList<Category>, private val presenter: RequestPresenter,private val selectedCategory:Category?) : Item() {

    private var showArray: Array<String>?=null


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {

            if (showArray == null) {
                showArray = categories.map { it.name }.toTypedArray()
                val hintSpinner = HintSpinner<String>(
                        spinner,
                        HintAdapter(context, R.string.register_event_select_category_hint, showArray?.toList()),
                        HintSpinner.Callback<String> { position, itemAtPosition ->
                            presenter.onDataChange("category_id",categories[position].id)
                        })
                hintSpinner.init()

                selectedCategory?.let {selectedCategory->
                    categories.forEach {
                        if(selectedCategory.id == it.id){
                            spinner.setSelection(categories.indexOf(it))
                            presenter.onDataChange("category_id",it.id)
                        }
                    }
                }

            }

        }
    }

    override fun getLayout() = R.layout.register_event_drop_down
}