package com.example.ui.eventDocuments

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.Document
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_documents_list.*
import kotlinx.android.synthetic.main.item_document.*
import javax.inject.Inject
import javax.inject.Provider

class DocumentsListFragment : BaseFragment(), DocumentsListContract.View {

    @InjectPresenter
    lateinit var presenter: DocumentsListPresenter

    @Inject
    lateinit var presenterProvider: Provider<DocumentsListPresenter>

    @ProvidePresenter
    fun providePresenter(): DocumentsListPresenter = presenterProvider.get().apply {
        val data = DocumentsListFragmentArgs.fromBundle(arguments)
        event = data.event
    }

    private val adapter: SimplePagingRecyclerViewAdapter<Document> by lazy {
        object : SimplePagingRecyclerViewAdapter<Document>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_document

            override fun onBindItem(viewHolder: ViewHolder, item: Document?, position: Int) {
                item!!
                viewHolder.apply {
                    itemView.setOnClickListener { presenter.onDocumentClick(item) }
                    tvDocumentName.text = item.name
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@DocumentsListFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, VERTICAL))
        }
    }

    override fun setData(documents: PagedList<Document>) {
        adapter.submitList(documents)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_documents_list
}
