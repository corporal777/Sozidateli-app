package com.example.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import com.example.app.R
import com.example.data.models.ChatMessage
import com.example.app.databinding.ItemChatMessageImageBinding
import com.example.extensions.dp
import com.example.util.RoundedCornersTransformation
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class ChatMessageImageItem(
        message: ChatMessage.Personal,
        private val onImageClick: (url: String, imageView: ImageView) -> Unit
) : ChatMessageItem<ItemChatMessageImageBinding>(message) {

    private val imageUrl = message.message.message

    override fun bind(viewBinding: ItemChatMessageImageBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
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

    override fun getGuidLineStart(binding: ItemChatMessageImageBinding): Guideline = binding.guidelineStart
    override fun getGuidLineEnd(binding: ItemChatMessageImageBinding): Guideline = binding.guidelineEnd
    override fun getMessageContainer(binding: ItemChatMessageImageBinding): View = binding.imageContainer
    override fun getDateView(binding: ItemChatMessageImageBinding): TextView = binding.tvMessageDate
    override fun getLayout() = R.layout.item_chat_message_image
}