package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.R
import com.example.data.models.ChatMessage
import com.example.data.models.User
import com.example.data.models.UserChat
import com.example.ui.chatList.ChatListPresenter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_list.*

class ChatListAdapter(options: FirestoreRecyclerOptions<UserChat>, private val presenter: ChatListPresenter) : FirestoreRecyclerAdapter<UserChat, ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemLayout(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserChat) {
        holder.apply {
            //model.user?.let {
            tvName.text = model.userId
            Picasso.get()
                    .load(R.drawable.ic_launcher)
                    .into(ivAvatar)
            // }
            model.lastMessage?.let { chatMessage ->
                chatMessage.text?.let {
                    tvLastMessage.text = it
                }
            }
            itemView.setOnClickListener { presenter.onChatClick(model) }
        }
    }

    private fun getItemLayout(itemView: Int): Int {
        return R.layout.item_chat_list
    }

}