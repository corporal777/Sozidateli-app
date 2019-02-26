package com.example.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.data.models.UserChatMessage
import com.example.util.chat.QueryList
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_message_incoming.*
import kotlinx.android.synthetic.main.item_chat_message_incoming.view.*

class ChatAdapter : RecyclerView.Adapter<ViewHolder>() {

    var queryList: QueryList<UserChatMessage>? = null

    var onItemClickListener: ((UserChatMessage,imageView: ImageView) -> Unit)? = null
    var onItemAttached: ((UserChatMessage) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemLayout(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        queryList?.get(holder.adapterPosition)?.apply {
            onItemAttached?.invoke(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = queryList?.get(position)!!
        holder.apply {
            tvChatMessage.text = model.message.text
            tvChatMessage.visibility = View.VISIBLE
            flImage.visibility = View.GONE

            var padding = itemView.context.resources.getDimensionPixelSize(R.dimen.chat_message_padding_vertical)
            var background = if (getItemViewType(position) == TYPE_INCOMING) ContextCompat.getDrawable(itemView.context, R.drawable.background_chat_message_incoming) else ContextCompat.getDrawable(itemView.context, R.drawable.background_chat_message_outgoing)

            if (!model.message.image.isNullOrBlank()) {
                tvChatMessage.visibility = View.GONE
                flImage.visibility = View.VISIBLE
                padding = 0
                val radius = itemView.context.resources.getDimensionPixelSize(R.dimen.chat_message_corners_radius)

                /*val listOfTransformations = listOf(
                        RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.TOP),
                        RoundedCornersTransformation(radius, 0,
                                if (getItemViewType(position) == TYPE_INCOMING) RoundedCornersTransformation.CornerType.BOTTOM_RIGHT
                                else RoundedCornersTransformation.CornerType.BOTTOM_LEFT)

                )*/

                Picasso.get().load(model.message.image)/*.transform(listOfTransformations)*/.into(ivImage, object : Callback {
                    override fun onSuccess() {
                        root.background = null
                    }

                    override fun onError(e: Exception?) {
                    }
                })
            }

            root.background = background
            itemView.subRoot.setPadding(padding, padding, padding, padding)

            root.setOnClickListener { onItemClickListener?.invoke(model,ivImage) }
        }
    }

    override fun getItemCount() = queryList?.size ?: 0

    private fun getItemLayout(itemView: Int): Int {
        return if (itemView == TYPE_OUTGOING) R.layout.item_chat_message_outgoing
        else R.layout.item_chat_message_incoming
    }

    override fun getItemViewType(position: Int): Int {
        val userMessage = queryList?.get(position)
        return if (userMessage?.isMyMessage == true) TYPE_OUTGOING else TYPE_INCOMING
    }

    companion object {
        private const val TYPE_INCOMING = 0
        private const val TYPE_OUTGOING = 1
    }
}