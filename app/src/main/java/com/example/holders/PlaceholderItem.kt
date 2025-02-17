package com.example.holders

import com.example.app.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class PlaceholderItem(
    private val type: Type
) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout() = when (type) {
        Type.SPEAKER_LIST -> R.layout.item_speaker_list_placeholder
        Type.SPEAKER_MAIN -> R.layout.item_speaker_placeholder

        //Type.EVENT -> R.layout.item_event_placeholder
        Type.EVENT -> R.layout.item_event_new_placeholder
        Type.EVENT_MAIN -> R.layout.item_event_main_placeholder
        Type.SEARCH_EVENT -> R.layout.item_search_event_placeholder

        Type.USER -> R.layout.item_user_placeholder
        Type.USER_PROFILE -> R.layout.item_user_profile_placeholder

        Type.CHAT_LIST -> R.layout.item_user_chat_list_placeholder
        Type.CHAT -> R.layout.item_user_chat_placeholder

        Type.SESSIONS -> R.layout.item_sessions_placeholder
        Type.ACCOUNTS -> R.layout.item_accounts_placeholder

        Type.ORGANIZATION -> R.layout.item_organization_placeholder
        Type.ORGANIZATION_MAIN -> R.layout.item_organization_main_placeholder

        Type.NOTIFICATION -> R.layout.item_notification_placeholder
        Type.NOTIFICATIONS_LIST -> R.layout.item_notifications_list_placeholder

        Type.SCHEDULE_CALENDAR -> R.layout.item_schedule_calendar_placeholder
        Type.SCHEDULE_LIST -> R.layout.item_schedule_list_placeholder
        Type.SUB_EVENT_MAIN -> R.layout.item_sub_event_main_placeholder

        Type.ACTIVITY_CALENDAR -> R.layout.item_activity_calendar_placeholder
        Type.ACTIVITY_LIST -> R.layout.item_activity_list_placeholder
        Type.ACTIVITY_TAGS -> R.layout.item_activity_tags_placeholder


        Type.INTERESTS -> R.layout.item_interests_placeholder
        Type.CONTACTS -> R.layout.item_contacts_placeholder

        Type.REGISTER_HEADER -> R.layout.item_event_register_header_placeholder
        Type.REGISTER_FIELD -> R.layout.item_event_register_field_placeholder

        Type.SEARCH_ITEM -> R.layout.item_search_placeholder

        Type.MAIN_INFO_EDIT -> R.layout.item_main_info_edit_placeholder
    }

    enum class Type {
        SPEAKER_LIST,
        SPEAKER_MAIN,

        EVENT,
        EVENT_MAIN,
        SEARCH_EVENT,

        ORGANIZATION,
        ORGANIZATION_MAIN,

        USER,
        USER_PROFILE,

        NOTIFICATION,
        NOTIFICATIONS_LIST,

        CHAT_LIST,
        CHAT,

        SESSIONS,
        ACCOUNTS,

        SCHEDULE_CALENDAR,
        SCHEDULE_LIST,

        ACTIVITY_CALENDAR,
        ACTIVITY_LIST,
        ACTIVITY_TAGS,

        SUB_EVENT_MAIN,

        INTERESTS,
        CONTACTS,

        REGISTER_HEADER,
        REGISTER_FIELD,

        SEARCH_ITEM,

        MAIN_INFO_EDIT
    }
}