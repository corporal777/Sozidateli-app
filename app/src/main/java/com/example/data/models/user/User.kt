package com.example.data.models.user

import android.net.Uri
import android.os.Parcelable
import com.example.data.models.ContactSearch
import com.example.data.models.Event
import com.example.data.models.Notification
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var user_id: Int = -1,
        var user_created: String = "",
        var user_modified: Boolean = false,
        var user_banned: Boolean = false,
        var user_suspended: Boolean = false,
        var user_email: String? = null,
        var user_email_show: Boolean = false,
        var user_email_confirmed: Boolean = false,
        var user_phone: String? = null,
        var user_phone_show: Boolean = false,
        var user_phone_country: String? = "",
        var user_phone_short: String? = "",
        var user_phone_confirmed: Boolean = false,
        var user_phone_work: String? = "",
        var user_phone_work_show: Boolean = false,
        var user_phone_work_country: String = "",
        var user_phone_work_short: String = "",
        var user_phone_work_confirmed: Boolean = false,
        var user_name: String = "",
        var user_middle_name: String = "",
        var user_last_name: String = "",
        var user_birthday: String? = null,
        var user_age: Int = -1,
        var user_birthday_show: Boolean = false,
        var user_avatar: String? = null,
        var user_avatar_uri: Uri? = null, //for update user avatar
        var user_gender: String? = null,
        var passport_country_id: String? = null,
        var passport_serial: String? = null,
        var passport_number: String? = null,
        var passport_given_by: String? = null,
        var passport_given_at: String? = null,
        var user_address: String? = null,
        var user_address_index: String? = null,
        var user_address_country: String? = null,
        var user_address_region: String? = null,
        var user_address_area: String? = null,
        var user_address_city: String? = null,
        var user_address_district: String? = null,
        var user_address_settlement: String? = null,
        var user_address_street: String? = null,
        var user_address_house: String? = null,
        var user_address_flat: String? = null,
        var user_notes: String? = null,
        var user_description: String? = null,
        var user_social_links: List<String>? = null,
        var emails: ArrayList<Value>? = null,
        var social_links: ArrayList<Value>? = null,
        var interests: ArrayList<Value>? = null,
        var academic_degree: ArrayList<Value>? = null,
        var memberships: ArrayList<Value>? = null,
        var settings_chat_allow_msg_from_all: Boolean = true,
        var settings_chat_allow_msg_from_fav: Boolean = true,
        //var settings_chat_disallow_msg_from_all: Boolean = false,
        var education: ArrayList<SocialRoles>? = null,
        var work: ArrayList<SocialRoles>? = null,
        var social_projects: ArrayList<SocialRoles>? = null,
        var default_event: Event? = null,
        //var web: ArrayList<Value>? = null
        var attached_recomendation_files: ArrayList<RecommendationFiles>? = null,
        var last_notification: Notification? = null,
        var notification_total: Int = -1,
        var notification_unread: Int = -1,
//        var event_status: String? = null,

        //support field
        var isEmailChanged:Boolean = false,
        var new_email:String?=null,


        //for search chat
        var is_in_favorite: Boolean = false,
        var is_has_chat: Boolean = false


        ) : Parcelable {
    var fullName: String = ""
        get() = "$user_name $user_last_name"

    var contactType: ContactSearch.Type = ContactSearch.Type.CONTACT
    get() {
        if(is_in_favorite) return ContactSearch.Type.FAVORITE
        if(is_has_chat) return ContactSearch.Type.CHAT
        return ContactSearch.Type.CONTACT
    }
}