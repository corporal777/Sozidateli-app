package com.examle.data.bodies

class EventsCalendarListBody {
    var sortField: String? = null
    val sortType: String = "desc"
    val limit: Int = 20
    val offset: Int = 0
    var binds: String? = "rights,organization,tag,page,activity,user-registration,user-form-result"
    var id: List<Int>? = null
    var name: List<String>? = null
    var code: List<String>? = null
    var createdDate: List<String>? = null
    var createdBy: List<Int>? = null
    var holdingDate: List<String>? = null
    var requestsApplyDateLimit: List<String>? = null
    var requestsApplyIsClosed: Boolean? = null
    var organization: List<Int>? = null
    var format: List<Int>? = null
    var formatCustom: List<String>? = null
    var regularity: List<Int>? = null
    var targetedAudience: List<Int>? = null
    var topicCategory: List<Int>? = null
    var topicSubcategories: List<Int>? = null
    var phone: List<String>? = null
    var phoneTitle: List<String>? = null
    var email: List<String>? = null
    var emailTitle: List<String>? = null
    var site: List<String>? = null
    var siteTitle: List<String>? = null
    var socialLink: List<String>? = null
    var socialLinkTitle: List<String>? = null
    var addressIndex: List<Int>? = null
    var addressCountry: List<String>? = null
    var addressFederal: List<String>? = null
    var addressRegion: List<String>? = null
    var addressArea: List<String>? = null
    var addressCity: List<String>? = null
    var addressSettlement: List<String>? = null
    var addressStreet: List<String>? = null
    var addressHouse: List<String>? = null
    var addressFlat: List<String>? = null
    var status: List<String>? = null
    var statusChanged: List<String>? = null
    var stateRegistrationIsAvailable: Boolean? = null
    var stateRegistrationFormEnabled: Boolean? = null
    var stateRegistrationApprovingMode: List<String>? = null
    var stateRatingFormEnabled: Boolean? = null
    var stateRatingAskDelay: List<Int>? = null
    var stateIsRunning: Boolean? = null
    var stateIsFinished: Boolean? = null
    var stateIsPublic: Boolean? = null
    var stateIsHidden: Boolean? = null

    fun toMap(): Map<String, Any> {
        val res: MutableMap<String, Any> = mutableMapOf()
        sortField?.let {
            res["sortField"] = it
        }
        sortType.let {
            res["sortType"] = it
        }
        limit.let {
            res["limit"] = it
        }
        offset.let {
            res["offset"] = it
        }
        binds?.let {
            res["binds"] = it
        }
        id?.let {
            res["id"] = it
        }
        name?.let {
            res["name"] = it
        }
        code?.let {
            res["code"] = it
        }
        createdDate?.let {
            res["createdDate"] = it
        }
        createdBy?.let {
            res["createdBy"] = it
        }
        holdingDate?.let {
            res["holdingDate"] = it
        }
        requestsApplyDateLimit?.let {
            res["requestsApplyDateLimit"] = it
        }
        requestsApplyIsClosed?.let {
            res["requestsApplyIsClosed"] = it
        }
        organization?.let {
            res["organization"] = it
        }
        format?.let {
            res["format"] = it
        }
        formatCustom?.let {
            res["formatCustom"] = it
        }
        regularity?.let {
            res["regularity"] = it
        }
        targetedAudience?.let {
            res["targetedAudience"] = it
        }
        topicCategory?.let {
            res["topicCategory"] = it
        }
        topicSubcategories?.let {
            res["topicSubcategories"] = it
        }
        phone?.let {
            res["phone"] = it
        }
        phoneTitle?.let {
            res["phoneTitle"] = it
        }
        email?.let {
            res["email"] = it
        }
        emailTitle?.let {
            res["emailTitle"] = it
        }
        site?.let {
            res["site"] = it
        }
        siteTitle?.let {
            res["siteTitle"] = it
        }
        socialLink?.let {
            res["socialLink"] = it
        }
        socialLinkTitle?.let {
            res["socialLinkTitle"] = it
        }
        addressIndex?.let {
            res["addressIndex"] = it
        }
        addressCountry?.let {
            res["addressCountry"] = it
        }
        addressFederal?.let {
            res["addressFederal"] = it
        }
        addressRegion?.let {
            res["addressRegion"] = it
        }
        addressArea?.let {
            res["addressArea"] = it
        }
        addressCity?.let {
            res["addressCity"] = it
        }
        addressSettlement?.let {
            res["addressSettlement"] = it
        }
        addressStreet?.let {
            res["addressStreet"] = it
        }
        addressHouse?.let {
            res["addressHouse"] = it
        }
        addressFlat?.let {
            res["addressFlat"] = it
        }
        status?.let {
            res["status"] = it
        }
        statusChanged?.let {
            res["statusChanged"] = it
        }
        stateRegistrationIsAvailable?.let {
            res["stateRegistrationIsAvailable"] = it
        }
        stateRegistrationFormEnabled?.let {
            res["stateRegistrationFormEnabled"] = it
        }
        stateRegistrationApprovingMode?.let {
            res["stateRegistrationApprovingMode"] = it
        }
        stateRatingFormEnabled?.let {
            res["stateRatingFormEnabled"] = it
        }
        stateRatingAskDelay?.let {
            res["stateRatingAskDelay"] = it
        }
        stateIsRunning?.let {
            res["stateIsRunning"] = it
        }
        stateIsFinished?.let {
            res["stateIsFinished"] = it
        }
        stateIsPublic?.let {
            res["stateIsPublic"] = it
        }
        stateIsHidden?.let {
            res["stateIsHidden"] = it
        }
        return res
    }
}