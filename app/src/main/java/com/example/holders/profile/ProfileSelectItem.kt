package com.example.holders.profile

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
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.util.Utils
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_select.view.*
import java.util.*
import kotlin.collections.ArrayList


class ProfileSelectItem(private val profileField: ProfileField, private val data: LinkedHashMap<String, String?>) : ProfileBaseFieldItem(profileField) {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var showArray: Array<String>


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            profileField.label?.let {
                tvFieldLabel.text = it
            }

            if (spinner.adapter == null) {
                showArray = data.map { it.key }.toTypedArray()
                adapter = ArrayAdapter(context, R.layout.spinner_item, showArray)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                        profileField.data = data[showArray[pos]]
                    }
                }

                profileField.data?.let {
                    val indexSelection = showArray.indexOf(it.toString().capitalize())
                    if (indexSelection != -1) {
                        spinner.setSelection(indexSelection)
                    }
                }
            }

        }
    }

    override fun getLayout() = R.layout.field_select
}