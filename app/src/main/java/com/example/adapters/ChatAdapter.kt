package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.R
import com.example.data.models.ChatMessage
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.item_chat_message_incoming.*

class ChatAdapter(options: FirestoreRecyclerOptions<ChatMessage>) : FirestoreRecyclerAdapter<ChatMessage, ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemLayout(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatMessage) {
        holder.apply {
            tvChatMessage.text = model.message
        }
    }

    private fun getItemLayout(itemView: Int): Int {
        return if (itemView == TYPE_OUTGOING) R.layout.item_chat_message_outgoing
        else R.layout.item_chat_message_incoming
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isMyMessage) TYPE_OUTGOING else TYPE_INCOMING
    }

    companion object {
        private const val TYPE_INCOMING = 0
        private const val TYPE_OUTGOING = 1
    }
}