package com.example.holders

import android.view.View
import androidx.constraintlayout.widget.Guideline
import com.example.R
import com.example.data.models.UserChatMessage
import com.example.util.RoundedCornersTransformation
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat_message_image.*

class ChatMessageImageItem(
        message: UserChatMessage
) : ChatMessageItem(message) {

    val imageUrl = message.message.image!!

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            ivChatImage.apply {
                Picasso.get().load(imageUrl)
                        .transform(RoundedCornersTransformation(
                                (cornersRadius / 1.5).toInt(),
                                0,
                                if (message.isMyMessage) RoundedCornersTransformation.CornerType.OTHER_BOTTOM_RIGHT else
                                    RoundedCornersTransformation.CornerType.OTHER_BOTTOM_LEFT
                        ))
                        .into(this)
            }
        }
    }

    override fun getGuidLineStart(viewHolder: ViewHolder): Guideline = viewHolder.guidelineStart
    override fun getGuidLineEnd(viewHolder: ViewHolder): Guideline = viewHolder.guidelineEnd
    override fun getMessageContainer(viewHolder: ViewHolder): View = viewHolder.imageContainer
    override fun getLayout() = R.layout.item_chat_message_image
}