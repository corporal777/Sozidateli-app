package com.example.util

import android.Manifest

const val DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR = "%02d.%02d.%d"
const val DATE_TIME_STRING_FORMAT_SHORT_MONTH_FULL_YEAR = "%02d.%02d.%d %02d:%02d"
const val DATE_FORMAT_SHORT_MONTH_FULL_YEAR = "dd.MM.yyyy"
const val DATE_FORMAT_SHORT_MONTH_NO_YEAR = "dd.MM"
const val DATE_FORMAT_SHORT_MONTH_SHORT_YEAR = "dd.MM.yy"
const val DATE_FORMAT_FULL_MONTH_NO_YEAR = "dd MMMM"
const val DATE_FORMAT_FULL_MONTH_FULL_YEAR = "dd MMMM yyyy"
const val DATE_FORMAT_SHORT_DAY_FULL_MONTH_FULL_YEAR = "d MMMM yyyy"
const val DATE_FORMAT_SHORT_DAY_SHORT_MONTH_FULL_YEAR_VK = "d.MM.yyyy"
const val DATE_FORMAT_SHORT_DAY_FULL_MONTH_SHORT_YEAR = "d MMMM"
const val DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE = "LLLL yyyy"
const val DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS = "MM.yyyy"
const val DATE_FORMAT_FULL_YEAR = "yyyy"
const val DATE_FORMAT_SERVER_TIMESTAMP = "yyyy-MM-dd"
const val DATE_TIME_FORMAT_SERVER_TIMESTAMP = "yyyy-MM-dd HH:mm:ss"
const val TIME_FORMAT_DEFAULT = "HH:mm"
const val DATE_TIME_FORMAT_DEFAULT = "dd.MM.yyyy HH:mm"
const val DATE_TIME_FORMAT_DEFAULT_NO_YEAR = "dd.MM HH:mm"
const val DATE_TIME_FORMAT_DEFAULT_FULL_MONTH = "dd MMMM yyyy HH:mm"
const val DATE_FORMAT_FULL_DAY_FULL_MONTH_NO_YEAR = "EEEE, dd MMMM"

const val AUTH_TOKEN_INVALID = "invalid"
const val AUTH_CONFIRM_EMAIL_EMAIL = "confirm_email"
const val AUTH_CONFIRM_EMAIL = "email"
const val AUTH_CONFIRM_EMAIL_CODE = "code"
const val AUTH_CONFIRM_SOCKET_ID = "id"
const val AUTH_CONFIRM_INVITE_ID = "invite"

const val RECOVERY_EMAIL = "recover_email"
const val CHANGE_EMAIL = "email"
const val USIP = "usip"


const val FIELD_CHAT = "chat"
const val FIELD_CHAT_ID = "chatId"
const val FIELD_LABEL = "label"
const val FIELD_NAME = "name"
const val FIELD_USER_ID = "userId"
const val FIELD_NOTIFICATION_ID = "notification_id"
const val FIELD_NOTIFICATION = "notification"
const val FIELD_EVENT = "event"
const val FIELD_EVENT_ID = "event_id"
const val FIELD_ORGANIZATION_ID = "organization_id"
const val FIELD_ACTION = "action"
const val PATH_SN_AUTHORIZATION = "social"
const val FIELD_SN_AUTHORIZATION_USER_ID = "usa_id"
const val PATH_EVENT = "event"
const val PATH_USER = "user"
const val PATH_PROFILE = "profile"
const val PATH_EVENT_MEMBER = "event-member"
const val PATH_AUTH = "auth"
const val PATH_SWITCH_ACCOUNT = "switch-account"
const val PATH_QR = "qr"
const val PATH_LP = "lp"
const val PATH_CHANGE_EMAIL = "email-change"
const val REGISTER_CONFIRM = "register-confirm"
const val PASSWORD_RECOVERY = "password-recovery"
const val PATH_CONFIRM_EMAIL = "email-confirm"
const val LINKED_REGISTER = "external-registration-confirm"
const val PGRF = "pgrf"
const val ASSISTANT = "assistant"
const val PATH_HIDDEN = "hidden"
const val PATH_SUPPORT_CENTER = "support_center"
const val PATH_SETTINGS = "settings"

const val REQUEST_CODE_SELECT_PDF = 12
const val REQUEST_CODE_PERMISSION_READ_FILE = 13

const val FIELD_ATTACH_RECOMMENDATION_FILE = "attached_recomendation_files"

const val GENDER_MALE = "M"
const val GENDER_FEMALE = "F"

const val SN_VK = "vk"
const val SN_GU = "gosUslugi"
const val SN_OK = "ok"
const val SN_FB = "fb"

const val ID = "id"
const val SET_EMAIL_USER_SOCIAL = "set_email_user_social"
const val SN_PROVIDER = "provider"


const val PART_ERROR_REQUEST_EVENT_FIELD_REQUIRED = "is required"
const val PART_ERROR_REQUEST_EVENT_REGISTER_END = "registration is not carried out"
const val PART_ERROR_REQUEST_EVENT_FILE_ERROR = "is not uploaded by current user"

const val BADGE_COUNT_MAX = 99
const val BADGE_TEXT_IF_MORE_THAN_MAX = "99+"

const val NAME = "name"

const val ACTION_REQUEST_COUNT = "chatRequestCount"
const val ACTION_ACCEPT = "chatAccept"
const val ACTION_INVITE = "chatInvite"
const val ACTION_BAN = "chatBan"
const val ACTION_UNBAN = "chatUnban"

const val CHAT_SERVICE_MESSAGE_ACCEPT = "accept"

const val IMAGE_MAX_SIZE_AVATAR = 1024
const val IMAGE_MAX_SIZE_CHAT = 1024

const val USER_DATA_EMPTY = "-"

const val REQUEST_GALLERY = 12
const val REQUEST_CAMERA = 15
val REQUIRED_GALLERY_PERMISSIONS =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

val REQUIRED_CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
const val DEGREES_MAX_SIZE = 10
const val PHONE_PERSONAL = "personal"
const val PHONE_WORK = "work"

const val PAGE_SIZE = 30
const val PAGE_PLACEHOLDER = false