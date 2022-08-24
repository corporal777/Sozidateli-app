package com.example.data.bodies

import com.example.data.models.FieldDetails
import com.example.data.models.FieldListDetails
import com.example.data.models.NewUserAddress
import com.example.data.models.UserState
import com.google.gson.annotations.SerializedName

data class RegisterBody(
        val password: String? = null,
        val name: String? = null,
        @SerializedName("lastName")
        val lastName: String? = null,
        @SerializedName("middleName")
        val middleName: FieldDetails? = null,
        val email: FieldDetails? = null,
        val phone: List<FieldDetails>? = null,
        val site: FieldListDetails? = null,
        @SerializedName("socialLinks")
        val socialLinks: FieldListDetails? = null,
        val birthday: FieldDetails? = null,
        val gender: String? = null,
        val address: NewUserAddress? = null,
        val state: UserState? = null,
        val interests: List<Int>? = null,
        val notes: String? = null,
        @SerializedName("educationLevel")
        val educationLevel: Int? = null,
        val deviceId : String,
        val deviceModel : String,
) {
        fun toMap(): Map<String, Any> {
                val res: MutableMap<String, Any> = mutableMapOf()
                password?.let {
                        res["password"] = it
                }
                name?.let {
                        res["name"] = it
                }
                lastName?.let {
                        res["lastName"] = it
                }
                middleName?.let {
                        res["middleName"] = it
                }
                email?.let {
                        res["email"] = it
                }
                phone?.let {
                        res["phone"] = it
                }
                site?.let {
                        res["site"] = it
                }
                socialLinks?.let {
                        res["socialLinks"] = it
                }
                birthday?.let {
                        res["birthday"] = it
                }
                gender?.let {
                        res["gender"] = it
                }
                address?.let {
                        res["address"] = it
                }
                state?.let {
                        res["state"] = it
                }
                interests?.let {
                        res["interests"] = it
                }
                notes?.let {
                        res["notes"] = it
                }
                educationLevel?.let {
                        res["educationLevel"] = it
                }
                return res
        }
}