package com.example.ui.views.suggestFieldView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.data.models.DataDataItem
import com.example.data.models.DataDataResponse


class AutoSuggestAdapter(context: Context, resource: Int) : ArrayAdapter<String>(context, resource) {


    private val data: MutableList<DataDataItem> = ArrayList()


    fun setData(list: List<DataDataItem>) {
        data.clear()
        data.addAll(list)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): String? {
        return data[position].unrestricted_value
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    fun getObject(position: Int): DataDataItem {
        return data[position]
    }
}