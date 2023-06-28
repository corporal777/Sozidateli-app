package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class PlaceholderItem(
    private val type: Type
) : Item() {

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

        Type.MAIN_SESSIONS -> R.layout.item_main_sessions_placeholder
        Type.OTHER_SESSIONS -> R.layout.item_other_sessions_placeholder
        Type.ACCOUNTS -> R.layout.item_accounts_placeholder

        Type.ORGANIZATION -> R.layout.item_organization_placeholder
        Type.ORGANIZATION_MAIN -> R.layout.item_organization_main_placeholder

        Type.NOTIFICATION -> R.layout.item_notification_placeholder
        Type.NOTIFICATIONS_LIST -> R.layout.item_notifications_list_placeholder
        Type.NOTIFICATIONS_MAIN -> R.layout.item_notifications_main_placeholder

        Type.SCHEDULE_CALENDAR -> R.layout.item_schedule_calendar_placeholder
        Type.SCHEDULE_LIST -> R.layout.item_schedule_list_placeholder
        Type.SUB_EVENT_MAIN -> R.layout.item_sub_event_main_placeholder
        Type.ACTIVITY_CALENDAR -> R.layout.item_activity_calendar_placeholder
        Type.ACTIVITY_LIST -> R.layout.item_activity_list_placeholder
        Type.INTERESTS -> R.layout.item_interests_placeholder
        Type.CONTACTS -> R.layout.item_contacts_placeholder
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
        NOTIFICATIONS_MAIN,

        CHAT_LIST,
        CHAT,
        ACCOUNTS,

        SCHEDULE_CALENDAR,
        SCHEDULE_LIST,

        ACTIVITY_CALENDAR,
        ACTIVITY_LIST,

        SUB_EVENT_MAIN,

        INTERESTS,
        CONTACTS,

        MAIN_SESSIONS,
        OTHER_SESSIONS,
    }
}