package com.example.holders.profile

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.R
import com.example.data.models.ProfileField
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.field_select.view.*
import java.util.*


class ProfileSelectItem(private val profileField: ProfileField, private val data: LinkedHashMap<String, String?>) : ProfileBaseFieldItem(profileField) {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var showArray: Array<String>
    private var selected: String? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.itemView.apply {
            profileField.label?.let {
                tvFieldLabel.text = it
            }

            if (profileField.data != GENDER_FEMALE && profileField.data != GENDER_MALE) {
                selected = profileField.data.toString()
            }

            showArray = data.map { it.key }.toTypedArray()
            adapter = ArrayAdapter(context, R.layout.spinner_item, showArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                    profileField.data = data[showArray[pos]]
                    selected = showArray[pos]
                }
            }

            selected?.let {
                val indexSelection = showArray.indexOf(it.capitalize())
                if (indexSelection != -1) {
                    spinner.setSelection(indexSelection)
                }
            }

        }
    }

    override fun getLayout() = R.layout.field_select
}