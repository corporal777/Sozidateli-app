package com.example.common.constants

import android.Manifest
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName

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

val SYSTEM_UI_LIGHT_STATUS_BAR =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) APPEARANCE_LIGHT_STATUS_BARS
        else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

@RequiresApi(Build.VERSION_CODES.R)
const val SYSTEM_UI_LIGHT_NAV_BAR = APPEARANCE_LIGHT_NAVIGATION_BARS

const val EVENT_SORT_FIELD = "sortField"
const val EVENT_SORT_TYPE = "sortType"
const val EVENT_LIMIT = "limit"
const val EVENT_ACTIVE = "onlyActive"
const val EVENT_OFFSET = "offset"
const val EVENT_BINDS = "binds"
const val EVENT_ID = "id"
const val EVENT_ORGANIZATION = "organization"
const val EVENT_SEARCH = "search"
const val EVENT_NAME = "name"
const val EVENT_START_DATE = "holdingDate"
const val EVENT_FORMAT = "format"
const val EVENT_CATEGORY = "topicCategory"
const val EVENT_STATUS = "status"
const val EVENT_STATUS_ALL = "cancelled,registration,registrationFinished,running,finished"
const val EVENT_HIDDEN = "stateIsHidden"
const val EVENT_PUBLIC = "isPublic"
const val EVENT_CODE = "code"
const val EVENT_USER_ID = "userRegistration.user"
const val EVENT_USER_STATUS = "userRegistration.status"
const val EVENT_ADDRESS_COUNTRY = "addressCountry"
const val EVENT_ADDRESS_CITY = "addressCity"
const val EVENT_ADDRESS_REGION = "addressRegion"
const val EVENT_ADDRESS_STREET = "addressStreet"

const val FILTER_REGISTRATION_PENDING = "pending"
const val FILTER_REGISTRATION_APPROVED = "approved"
const val FILTER_REGISTRATION_DECLINED = "declined"
const val FILTER_REGISTRATION_ANY_REGISTERED = "pending,approved,declined"

const val EVENT_STATUS_CANCELED = "cancelled"
const val EVENT_STATUS_PREPARING = "preparing"
const val EVENT_STATUS_AWAITING = "awaiting"
const val EVENT_STATUS_PENDING = "pending"
const val EVENT_STATUS_APPROVED = "approved"
const val EVENT_STATUS_DECLINED = "declined"
const val EVENT_STATUS_BANNED = "banned"
const val EVENT_STATUS_REGISTRATION = "registration"
const val EVENT_STATUS_REGISTRATION_FINISHED = "registrationFinished"
const val EVENT_STATUS_RUNNING = "running"
const val EVENT_STATUS_FINISHED = "finished"