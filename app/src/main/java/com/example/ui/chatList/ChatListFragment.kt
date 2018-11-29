package com.example.ui.chatList

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.ChatAdapter
import com.example.adapters.ChatListAdapter
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.holders.UserChatItem
import com.example.ui.base.BaseFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_chat_list.*
import javax.inject.Inject
import javax.inject.Provider

class ChatListFragment : BaseFragment(), ChatListContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }


    }

    override fun iniChatAdapter(query: Query) {
        val options = FirestoreRecyclerOptions.Builder<UserChat>()
                .setLifecycleOwner(this)
                .setQuery(query, UserChat::class.java)
                .build()

        recyclerView.adapter = ChatListAdapter(options,presenter)
    }

    override fun openChat(userChat: UserChat) {
        userChat.chatId?.let {
            findNavController().navigate(ChatListFragmentDirections.chatListToChat().setChatId(it))
        }
    }

    override fun layout() = R.layout.fragment_chat_list
}
