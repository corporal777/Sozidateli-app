package com.example.data.models

data class RegisterStatus(
        val user_by_email_found: Boolean,
        val user_by_email_confirmed_email: Boolean,
        val user_by_email_confirmed_tel: Boolean,
        val user_by_email_confirmed_work_tel: Boolean,
        val user_by_email_banned: Boolean,
        val user_by_email_suspended: Boolean,
        val social_auth_found: Boolean,
        val user_by_social_found: Boolean,
        val user_by_social_confirmed_email: Boolean,
        val user_by_social_confirmed_tel: Boolean,
        val user_by_social_confirmed_work_tel: Boolean,
        val user_by_social_banned: Boolean,
        val user_by_social_suspended: Boolean,
        val user_email_and_social_the_same: Boolean
)