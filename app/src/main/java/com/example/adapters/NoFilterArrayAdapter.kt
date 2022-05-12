package com.example.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class NoFilterArrayAdapter<T> : ArrayAdapter<T>, Filterable {

    private lateinit var filter: DummyFilter

    constructor(context: Context, resource: Int) : super(context, resource)
    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(context, resource, textViewResourceId)
    constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects)
    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: Array<T>) : super(context, resource, textViewResourceId, objects)
    constructor(context: Context, resource: Int, objects: MutableList<T>) : super(context, resource, objects)
    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: MutableList<T>) : super(context, resource, textViewResourceId, objects)

    override fun getFilter(): Filter {
        if (!::filter.isInitialized) filter = DummyFilter()
        return filter
    }

    private class DummyFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

        }
    }
}