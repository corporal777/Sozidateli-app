package com.example.holders

import com.example.util.chat.QueryList
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.xwray.groupie.Group
import com.xwray.groupie.GroupDataObserver
import com.xwray.groupie.Item

class QueryPageListGroup<T : Item<*>> : Group, ChangeEventListener {

    private val groupDataObservers = mutableListOf<GroupDataObserver>()

    private var queryList: QueryList<T>? = null

    fun setQueryList(queryList: QueryList<T>) {
        this.queryList?.also { it.removeChangeEventListener(this) }
        this.queryList = queryList
        performObserverStateChanged()
    }

    override fun registerGroupDataObserver(groupDataObserver: GroupDataObserver) {
        if (!groupDataObservers.contains(groupDataObserver) && groupDataObservers.add(groupDataObserver)) {
            performObserverStateChanged()
        }
    }

    override fun unregisterGroupDataObserver(groupDataObserver: GroupDataObserver) {
        if (groupDataObservers.remove(groupDataObserver)) {
            performObserverStateChanged()
        }
    }

    private fun performObserverStateChanged() {
        queryList?.also {
            if (groupDataObservers.isEmpty()) it.removeChangeEventListener(this)
            else if (!it.isListening(this)) it.addChangeEventListener(this)
        }
    }

    override fun getItemCount() = queryList?.size ?: 0

    override fun getItem(position: Int): Item<*> = queryList?.get(position)!!

    override fun getPosition(item: Item<*>): Int = queryList?.indexOf(item) ?: -1

    override fun onDataChanged() {

    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        val action: (GroupDataObserver) -> Unit = when (type) {
            ChangeEventType.ADDED -> { observer: GroupDataObserver -> observer.onItemInserted(this, newIndex) }
            ChangeEventType.CHANGED -> { observer: GroupDataObserver -> observer.onItemChanged(this, newIndex) }
            ChangeEventType.REMOVED -> { observer: GroupDataObserver -> observer.onItemRemoved(this, oldIndex) }
            ChangeEventType.MOVED -> { observer: GroupDataObserver -> observer.onItemMoved(this, oldIndex, newIndex) }
            else -> throw IllegalStateException("Incomplete when statement")
        }

        groupDataObservers.toList().forEach(action)
    }

    override fun onError(e: FirebaseFirestoreException) {

    }
}