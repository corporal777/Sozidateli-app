package com.example.ui.views.suggestFieldView

import android.content.Context
import com.example.data.models.DaDataItem
import com.example.data.models.NewUserAddress
import com.example.data.models.UserAddress
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

object DaDataUtil {

    fun getLocationJson(context: Context) = JSONObject(getJsonFromAssets(context, "Locations.json"))

    fun formatLocations(context: Context, data: List<DaDataItem>): List<DaDataItem> {
        val jObject = getLocationJson(context)
        data.forEach {
            it.unrestricted_value = formatParam(it.unrestricted_value, jObject)?: ""
            it.value = formatParam(it.value, jObject)?: ""
            it.data.postal_code = formatParam(it.data.postal_code, jObject)
            it.data.country = formatParam(it.data.country, jObject)
            it.data.federal_district = formatParam(it.data.federal_district, jObject)
            it.data.region_with_type = formatParam(it.data.region_with_type, jObject)
            it.data.area_with_type = formatParam(it.data.area_with_type, jObject)
            it.data.city_with_type = formatParam(it.data.city_with_type, jObject)
            it.data.city_district_with_type = formatParam(it.data.city_district_with_type, jObject)
            it.data.settlement_with_type = formatParam(it.data.settlement_with_type, jObject)
            it.data.street_with_type = formatParam(it.data.street_with_type, jObject)
            it.data.house = formatParam(it.data.house, jObject)
            it.data.flat = formatParam(it.data.flat, jObject)
        }
        return data
    }

    fun formatSavedLocation(context: Context, data: NewUserAddress?): UserAddress {
        val jObject = getLocationJson(context)
        val address = formatParam(data?.city?.value, jObject)
        val index = formatParam(data?.index, jObject)
        val country = formatParam(data?.country, jObject)
        val federal = formatParam(data?.federal, jObject)
        val region = formatParam(data?.region, jObject)
        val area = formatParam(data?.area, jObject)
        val city = formatParam(data?.city?.value, jObject)
        val district = formatParam(data?.area, jObject)
        val settlement = formatParam(data?.settlement, jObject)
        val street = formatParam(data?.street, jObject)
        val house = formatParam(data?.house, jObject)
        val flat = formatParam(data?.flat, jObject)
        return UserAddress(address = address, index = index, country = country, federal = federal,
                region = region, area = area, city = city, district = district, settlement = settlement,
                street = street, house = house, flat = flat)
    }

    fun formatSavedLocation(context: Context, data: UserAddress): UserAddress {
        val jObject = getLocationJson(context)
        val address = formatParam(data.address, jObject)
        val index = formatParam(data.index, jObject)
        val country = formatParam(data.country, jObject)
        val federal = formatParam(data.federal, jObject)
        val region = formatParam(data.region, jObject)
        val area = formatParam(data.area, jObject)
        val city = formatParam(data.city, jObject)
        val district = formatParam(data.district, jObject)
        val settlement = formatParam(data.settlement, jObject)
        val street = formatParam(data.street, jObject)
        val house = formatParam(data.house, jObject)
        val flat = formatParam(data.flat, jObject)
        return UserAddress(address = address, index = index, country = country, federal = federal,
        region = region, area = area, city = city, district = district, settlement = settlement,
        street = street, house = house, flat = flat)
    }

    fun formatParam(data: String?, jsonObject: JSONObject): String? {
        var result: String? = null
        if (data?.isBlank() == false) {
            val unrestrictedValueSplit = data.split(" ").toMutableList()
            for (i in unrestrictedValueSplit.indices) {
                try {
                    unrestrictedValueSplit[i] = jsonObject[unrestrictedValueSplit[i]].toString()
                } catch (e: JSONException) {

                }
            }
            result = unrestrictedValueSplit.joinToString(" ")
        }
        return result
    }

    private fun getJsonFromAssets(context: Context, fileName: String?): String? {
        val jsonString: String
        jsonString = try {
            val `is`: InputStream = context.assets.open(fileName)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }
}