package com.example.data.models.user

import com.example.data.models.*
import com.example.ui.views.UserSubscribeButton
import com.example.util.USER_MIDDLE_NAME_EMPTY
import com.google.gson.annotations.SerializedName

data class User(
        var user_id: Int = -1,
        var user_created: String = "",
        var user_modified: Boolean = false,
        var user_banned: Boolean = false,
        var user_suspended: Boolean = false,
        var user_status: Status? = null,
        var user_status_detail: UserStatusDetails? = null,
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
        var user_status_phone: String? = "",
        var user_status_phone_country: String = "",
        var user_status_phone_short: String = "",
        var user_status_phone_confirmed: Boolean = false,
        var user_name: String = "",
        var user_middle_name: String? = "",
        var user_last_name: String = "",
        var user_birthday: String? = null,
        var user_age: Int = -1,
        var user_birthday_show: Boolean = false,
        var user_avatar: String? = null,
        var user_gender: String? = null,
        var passport_country_id: String? = null,
        var passport_serial: String? = null,
        var passport_number: String? = null,
        var passport_given_by: String? = null,
        var passport_given_at: String? = null,
        var user_city: String? = null,
        var user_address: String? = null,
        var user_short_address: String? = null,
        var user_address_index: String? = null,
        var user_address_country: String? = null,
        var user_address_federal: String? = null,
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
        var emails: List<Value>? = null,
        var social_links: List<UserDataSocialLink>? = null,
        var interests: List<Interest>? = null,
        var memberships: List<Value>? = null,
        var settings_chat_allow_msg_from_all: Boolean = true,
        var settings_chat_allow_msg_from_fav: Boolean = true,
        val chat: UserChat? = null,
        var user_education: String? = null,
        var education: List<SocialRoles>? = null,
        var work: List<SocialRoles>? = null,
        var social_projects: List<SocialRoles>? = null,
        var default_event: Event? = null,
        val organisations: List<Organization>? = null,
        var attached_recomendation_files: List<RecommendationFile>? = null,
        var last_notification: Notification? = null,
        var notification_total: Int = -1,
        var notification_unread: Int = -1,
        var is_in_favorite: Boolean = false,
        var is_has_chat: Boolean = false,
        var academic_degree: List<AcademicDegree>? = null,
        val available_degrees: List<String>? = null,
        val available_sciences: List<String>? = null,
        val available_education: List<String>? = null,
        var site: String? = null
) {
    var isCurrentUser = false

    val fullName: String
        get() {
            val nameList = listOfNotNull(
                    user_name,
                    getMiddleName(),
                    user_last_name
            )
            return nameList.joinToString(" ")
        }

    fun getMiddleName(): String? {
        return user_middle_name?.let { if (it == USER_MIDDLE_NAME_EMPTY || it.isEmpty()) null else it }
    }

    fun getUserSubscribeAction(): UserSubscribeButton.Action? {
        return when {
            isCurrentUser -> null
            user_banned || chat?.isBannedByYou == true -> UserSubscribeButton.Action.UNBLOCK
            is_in_favorite -> UserSubscribeButton.Action.UNFAVORITE
            else -> UserSubscribeButton.Action.FAVORITE
        }
    }

    companion object {
        const val FIELD_USER_NAME = "user_name"
        const val FIELD_USER_LAST_NAME = "user_last_name"
        const val FIELD_USER_MIDDLE_NAME = "user_middle_name"
        const val FIELD_USER_AVATAR = "user_avatar"
        const val FIELD_USER_EMAIL = "user_email"
        const val FIELD_USER_EMAIL_SHOW = "user_email_show"
        const val FIELD_USER_PHONE_WORK = "user_phone_work"
        const val FIELD_USER_PHONE_WORK_SHOW = "user_phone_work_show"
        const val FIELD_USER_PHONE_MOBILE = "user_phone"
        const val FIELD_USER_PHONE_MOBILE_SHOW = "user_phone_show"
        const val FIELD_USER_GENDER = "user_gender"
        const val FIELD_USER_BIRTHDAY = "user_birthday"
        const val FIELD_USER_BIRTHDAY_SHOW = "user_birthday_show"
        const val FIELD_USER_ADDRESS = "user_address"
        const val FIELD_USER_ADDRESS_INDEX = "user_address_index"
        const val FIELD_USER_ADDRESS_COUNTRY = "user_address_country"
        const val FIELD_USER_ADDRESS_FEDERAL = "user_address_federal"
        const val FIELD_USER_ADDRESS_REGION = "user_address_region"
        const val FIELD_USER_ADDRESS_AREA = "user_address_area"
        const val FIELD_USER_ADDRESS_CITY = "user_address_city"
        const val FIELD_USER_ADDRESS_CITY_GPS_LAT = "user_address_city_gps_lat"
        const val FIELD_USER_ADDRESS_CITY_GPS_LON = "user_address_city_gps_lon"
        const val FIELD_USER_ADDRESS_DISTRICT = "user_address_district"
        const val FIELD_USER_ADDRESS_SETTLEMENT = "user_address_settlement"
        const val FIELD_USER_ADDRESS_STREET = "user_address_street"
        const val FIELD_USER_ADDRESS_HOUSE = "user_address_house"
        const val FIELD_USER_ADDRESS_FLAT = "user_address_flat"
        const val FIELD_SOCIAL_LINKS = "social_links"
        const val FIELD_USER_OLD_PASSWORD = "user_old_password"
        const val FIELD_USER_NEW_PASSWORD = "user_new_password"
        const val FIELD_USER_EDUCATION = "user_education"
        const val FIELD_EDUCATION = "education"
        const val FIELD_ACADEMIC_DEGREE = "academic_degree"
        const val FIELD_WORK = "work"
        const val FIELD_INTERESTS = "interests"
        const val FIELD_USER_NOTES = "user_notes"
        const val FIELD_ATTACHED_FILES = "attached_recomendation_files"
        const val FIELD_USER_STATUS_PHONE = "user_status_phone"
        const val FIELD_USER_IS_IN_FAVORITE = "is_in_favorite"
    }

    enum class Status {
        @SerializedName("LOW_PROTECTION")
        LOW_PROTECTION,

        @SerializedName("MID_PROTECTION")
        MID_PROTECTION,

        @SerializedName("MAX_PROTECTION")
        MAX_PROTECTION
    }
}