package com.example.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import com.example.R
import com.example.data.models.ChatMessage
import com.example.extensions.dp
import com.example.util.RoundedCornersTransformation
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat_message_image.*
import kotlinx.android.synthetic.main.item_chat_message_image.guidelineEnd
import kotlinx.android.synthetic.main.item_chat_message_image.guidelineStart
import kotlinx.android.synthetic.main.item_chat_message_image.tvMessageDate

class ChatMessageImageItem(
        message: ChatMessage.Personal,
        private val onImageClick: (url: String, imageView: ImageView) -> Unit
) : ChatMessageItem(message) {

    private val imageUrl = message.message.message

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            pbImageLoading.visibility = View.VISIBLE
            ivChatImage.apply {
                transitionName = message.message._id
                Picasso.get().load(imageUrl)
                        .centerCrop()
                        .resize(242.dp, 242.dp)
                        .error(R.drawable.ic_broken_image)
                        .transform(RoundedCornersTransformation(
                                (cornersRadius / 1.3).toInt(),
                                0
                        ))
                        .into(this, object : Callback {
                            override fun onSuccess() {
                                pbImageLoading.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                pbImageLoading.visibility = View.GONE
                            }
                        })

                setOnClickListener { onImageClick(imageUrl, ivChatImage) }
            }
        }
    }

    override fun getGuidLineStart(viewHolder:GroupieViewHolder): Guideline = viewHolder.guidelineStart
    override fun getGuidLineEnd(viewHolder:GroupieViewHolder): Guideline = viewHolder.guidelineEnd
    override fun getMessageContainer(viewHolder:GroupieViewHolder): View = viewHolder.imageContainer
    override fun getDateView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvMessageDate
    override fun getLayout() = R.layout.item_chat_message_image
}